//! Per-domain allow-list for portal origins (Refs #103).
//!
//! Rules: explicit domains only, no wildcards, no "remember for all domains"
//! bypass. Entries land here ONLY through the per-request confirmation
//! dialog. Dependency-free: testable with bare `rustc --test`.

use std::collections::BTreeSet;

/// Allow-list: sorted unique lowercase domains.
#[derive(Debug, Clone, Default, PartialEq, Eq)]
pub struct AllowList {
    domains: BTreeSet<String>,
}

/// Allow-list errors.
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum AllowError {
    BadDomain,
    /// Caller tried a wildcard (`*.example.es` / `*`).
    Wildcard,
}

impl std::fmt::Display for AllowError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::BadDomain => write!(f, "not a valid domain"),
            Self::Wildcard => write!(f, "wildcards are not allowed: list each domain"),
        }
    }
}

/// Normalise one domain: lowercase, no trailing dot, ASCII letters/digits/`.`/`-`.
pub fn normalise_domain(input: &str) -> Result<String, AllowError> {
    let d = input.trim().to_ascii_lowercase();
    let d = d.strip_suffix('.').unwrap_or(&d);
    if d.contains('*') {
        return Err(AllowError::Wildcard);
    }
    if d.is_empty()
        || d.len() > 253
        || !d
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || b == b'.' || b == b'-')
        || d.split('.')
            .any(|label| label.is_empty() || label.len() > 63)
    {
        return Err(AllowError::BadDomain);
    }
    Ok(d.to_string())
}

impl AllowList {
    pub fn new() -> Self {
        Self::default()
    }

    pub fn from_iter<I: IntoIterator<Item = String>>(items: I) -> Result<Self, AllowError> {
        let mut list = Self::new();
        for item in items {
            list.add(&item)?;
        }
        Ok(list)
    }

    /// Exact-match check. `None` origin (no provable return URL) is never allowed.
    pub fn contains(&self, origin: Option<&str>) -> bool {
        match origin {
            Some(o) => self.domains.contains(o),
            None => false,
        }
    }

    /// Add one explicit domain. Returns `false` when already present.
    pub fn add(&mut self, domain: &str) -> Result<bool, AllowError> {
        Ok(self.domains.insert(normalise_domain(domain)?))
    }

    /// Remove one domain. Returns `false` when absent.
    pub fn remove(&mut self, domain: &str) -> bool {
        self.domains.remove(&domain.trim().to_ascii_lowercase())
    }

    pub fn domains(&self) -> impl Iterator<Item = &str> {
        self.domains.iter().map(String::as_str)
    }

    pub fn len(&self) -> usize {
        self.domains.len()
    }

    pub fn is_empty(&self) -> bool {
        self.domains.is_empty()
    }

    /// Serialise: one domain per line (the on-disk format). Sorted by construction.
    pub fn to_text(&self) -> String {
        let mut out = String::new();
        for d in &self.domains {
            out.push_str(d);
            out.push('\n');
        }
        out
    }

    /// Parse the on-disk format, skipping blanks and `#` comments.
    /// Invalid lines fail loudly — a corrupt list must surface, never silently shrink.
    pub fn from_text(text: &str) -> Result<Self, AllowError> {
        let mut list = Self::new();
        for line in text.lines() {
            let line = line.trim();
            if line.is_empty() || line.starts_with('#') {
                continue;
            }
            list.add(line)?;
        }
        Ok(list)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn exact_match_only_no_bypass() {
        let list = AllowList::from_iter(["sede.example.es".to_string()]).unwrap();
        assert!(list.contains(Some("sede.example.es")));
        assert!(!list.contains(Some("evil.example.es")));
        assert!(!list.contains(Some("sub.sede.example.es")));
        assert!(!list.contains(None));
    }

    #[test]
    fn wildcards_and_junk_rejected() {
        let mut list = AllowList::new();
        assert_eq!(list.add("*.example.es"), Err(AllowError::Wildcard));
        assert_eq!(list.add("*"), Err(AllowError::Wildcard));
        assert_eq!(list.add("not a domain"), Err(AllowError::BadDomain));
        assert_eq!(list.add(""), Err(AllowError::BadDomain));
        assert!(list.is_empty());
    }

    #[test]
    fn round_trip_with_comments() {
        let mut list = AllowList::new();
        assert!(list.add("Sede.Example.ES.").unwrap());
        assert!(!list.add("sede.example.es").unwrap());
        assert_eq!(list.to_text(), "sede.example.es\n");
        let back = AllowList::from_text("# portals\nsede.example.es\n\n").unwrap();
        assert_eq!(back.len(), 1);
        assert!(back.contains(Some("sede.example.es")));
    }

    #[test]
    fn corrupt_line_fails_loudly() {
        assert_eq!(
            AllowList::from_text("good.example.es\nnot a domain\n"),
            Err(AllowError::BadDomain)
        );
    }

    #[test]
    fn remove_is_case_insensitive() {
        let mut list = AllowList::from_iter(["sede.example.es".to_string()]).unwrap();
        assert!(list.remove("SEDE.EXAMPLE.ES"));
        assert!(list.is_empty());
        assert!(!list.remove("sede.example.es"));
    }
}
