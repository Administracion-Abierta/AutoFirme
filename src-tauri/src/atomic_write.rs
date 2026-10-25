use std::fs::{self, File};
use std::io::{self, Write};
use std::path::{Path, PathBuf};
use std::sync::atomic::{AtomicU64, Ordering};

static COUNTER: AtomicU64 = AtomicU64::new(0);

/// Write `bytes` to `path` so a crash leaves either the previous file or the new one,
/// never a half-written file: data goes to a sibling temp file, is fsynced, then renamed
/// over the target. The directory is fsynced afterwards so the rename itself survives.
///
/// The rename-over-existing behaviour is POSIX; this shell targets Linux (see #19).
pub fn write_atomic(path: &Path, bytes: &[u8]) -> io::Result<()> {
    let dir = match path.parent() {
        Some(parent) if !parent.as_os_str().is_empty() => parent,
        _ => Path::new("."),
    };
    let temp = temp_path(path, dir);
    let result = (|| {
        let mut file = File::create(&temp)?;
        file.write_all(bytes)?;
        file.sync_all()?;
        fs::rename(&temp, path)
    })();
    if result.is_err() {
        let _ = fs::remove_file(&temp);
    }
    result?;
    if let Ok(directory) = File::open(dir) {
        let _ = directory.sync_all();
    }
    Ok(())
}

/// Sibling temp name: same filesystem (so `rename` is atomic) and unique per write.
fn temp_path(path: &Path, dir: &Path) -> PathBuf {
    let name = path
        .file_name()
        .and_then(|name| name.to_str())
        .unwrap_or("file");
    dir.join(format!(
        ".{}.{}.{}.tmp",
        name,
        std::process::id(),
        COUNTER.fetch_add(1, Ordering::Relaxed)
    ))
}

#[cfg(test)]
mod tests {
    use super::*;

    fn scratch(name: &str) -> PathBuf {
        let dir = std::env::temp_dir().join(format!("autofirme-atomic-{}-{}", std::process::id(), name));
        let _ = fs::remove_dir_all(&dir);
        fs::create_dir_all(&dir).unwrap();
        dir
    }

    #[test]
    fn writes_a_new_file() {
        let dir = scratch("new");
        let target = dir.join("signed.pdf");
        write_atomic(&target, b"%PDF-1.7").unwrap();
        assert_eq!(fs::read(&target).unwrap(), b"%PDF-1.7");
        assert_eq!(fs::read_dir(&dir).unwrap().count(), 1, "no temp file left behind");
    }

    #[test]
    fn replaces_an_existing_file_and_cleans_up_on_failure() {
        let dir = scratch("replace");
        let target = dir.join("documento.txt");
        fs::write(&target, b"old").unwrap();
        write_atomic(&target, b"new").unwrap();
        assert_eq!(fs::read(&target).unwrap(), b"new");
        // A directory as target makes the rename fail; the temp file must not survive.
        let bad = dir.join("sub");
        fs::create_dir(&bad).unwrap();
        assert!(write_atomic(&bad, b"nope").is_err());
        assert_eq!(fs::read_dir(&dir).unwrap().count(), 2, "target plus the failing subdir");
    }
}
