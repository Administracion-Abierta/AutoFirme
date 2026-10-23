// Peer identity: sha256 fingerprint of the QUIC TLS certificate (Refs #110).
//
// Shown as words for out-of-band comparison (TOFU store material for #111);
// hex stays the machine-readable form. Pure functions, no I/O.

use sha2::{Digest, Sha256};

/// 32-byte fingerprint of a DER-encoded certificate.
#[derive(Clone, Copy, PartialEq, Eq, Hash)]
pub struct Fingerprint(pub [u8; 32]);

impl Fingerprint {
    pub fn of_der(der: &[u8]) -> Self {
        Self(Sha256::digest(der).into())
    }

    pub fn hex(&self) -> String {
        let mut out = String::with_capacity(64);
        for byte in self.0 {
            out.push_str(&format!("{byte:02x}"));
        }
        out
    }

    pub fn parse_hex(hex: &str) -> Option<Self> {
        // Byte length alone is not enough: slicing a multibyte char would
        // panic. TXT values come from the LAN, so a hostile peer could kill
        // the browse task with a 64-byte non-ASCII `fp` (review #110).
        if hex.len() != 64 || !hex.is_ascii() {
            return None;
        }
        let mut bytes = [0u8; 32];
        for (i, chunk) in bytes.iter_mut().enumerate() {
            *chunk = u8::from_str_radix(&hex[2 * i..2 * i + 2], 16).ok()?;
        }
        Some(Self(bytes))
    }

    /// Short words form for reading over the phone: 8 groups of 4 words
    /// (`word-word-word-word`), one word per byte, 4 bytes per group.
    /// Not BIP-39 (stdlib-free); a fixed 256-word list indexed by byte value.
    pub fn words(&self) -> String {
        self.0
            .chunks(4)
            .map(|group| {
                group
                    .iter()
                    .map(|byte| WORDS[*byte as usize])
                    .collect::<Vec<_>>()
                    .join("-")
            })
            .collect::<Vec<_>>()
            .join(" ")
    }
}

impl std::fmt::Debug for Fingerprint {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Fingerprint({})", &self.hex()[..16])
    }
}

impl std::fmt::Display for Fingerprint {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.hex())
    }
}

// ponytail: 256 common Spanish/English short words; upgrade path is a real
// wordlist (e.g. PGP biometrics) when UX asks for it.
const WORDS: [&str; 256] = [
    "alba", "actor", "agua", "aire", "ala", "albor", "altar", "alto", "amigo", "ancla", "andar",
    "angel", "anillo", "arbol", "arco", "arena", "arte", "atlas", "autor", "ave", "avion", "azul",
    "balsa", "banco", "banda", "barco", "barrio", "base", "bateria", "bodega", "blanco", "bloque",
    "boca", "bolsa", "bomba", "borde", "bosque", "boton", "bravo", "brisa", "bronce", "buzon",
    "cable", "cadena", "caja", "calle", "calma", "campo", "canal", "canto", "caoba", "capitan",
    "cara", "carbon", "carga", "carro", "carta", "casa", "casco", "causa", "cedro", "centro",
    "cerco", "cerro", "cielo", "cinta", "circo", "clave", "clima", "cobre", "codigo", "cola",
    "color", "columna", "cometa", "compas", "condor", "consejo", "copa", "costa", "cresta",
    "cruz", "cuadro", "cuarto", "cuenta", "cuerda", "cueva", "cumbre", "curso", "dado", "delta",
    "denso", "destino", "diamante", "diario", "dique", "disco", "domo", "dorso", "dragon",
    "dulce", "eco", "eje", "emblema", "encina", "enlace", "escala", "esfera", "espada",
    "espiga", "estela", "etapa", "faro", "fibra", "fiesta", "filtro", "finca", "firma",
    "flecha", "flora", "foco", "forma", "foro", "frente", "fresa", "fuente", "farol",
    "galeria", "garaje", "girasol", "globo", "golpe", "gota", "grano", "gruta", "guia",
    "halcon", "harpa", "helada", "hielo", "hierro", "hogar", "hoja", "horizonte", "huella",
    "humo", "isla", "jardin", "junta", "lago", "lampara", "lapiz", "lente", "libro",
    "lider", "linea", "lista", "llave", "lluvia", "lobo", "loma", "luna", "madera",
    "magneto", "mango", "manzana", "mapa", "marco", "marea", "margen", "marfil", "mazorca",
    "medalla", "mercado", "mesa", "metal", "mirador", "mitad", "modelo", "molino",
    "montana", "muro", "museo", "nave", "niebla", "nido", "nieve", "nivel", "norte",
    "nube", "nucleo", "oasis", "ocaso", "oferta", "olivo", "onda", "orilla", "orquidea",
    "oveja", "palma", "panel", "papel", "parque", "paso", "patio", "piedra", "pino",
    "pista", "planeta", "plata", "playa", "plaza", "pluma", "polar", "polea", "portal",
    "prado", "presa", "puente", "puerto", "pulso", "punto", "quinta", "radar", "rama",
    "rampa", "rayo", "recta", "refugio", "reloj", "remanso", "retina", "rio", "roble",
    "roca", "rodaje", "rumbo", "ruta", "salto", "selva", "sendero", "sierra", "signo",
    "silla", "sirena", "solar", "sonda", "soplo", "tabla", "talud", "tambor", "tapia",
    "techo", "templo", "tesoro", "tierra",
];

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn hex_roundtrip() {
        let fp = Fingerprint::of_der(b"fake-cert-der");
        assert_eq!(fp.hex().len(), 64);
        assert_eq!(Fingerprint::parse_hex(&fp.hex()), Some(fp));
    }

    #[test]
    fn bad_hex_rejected() {
        assert_eq!(Fingerprint::parse_hex("zz"), None);
        assert_eq!(Fingerprint::parse_hex(&"ab".repeat(31)), None);
        assert_eq!(Fingerprint::parse_hex(&"zz".repeat(32)), None);
    }

    #[test]
    fn multibyte_hex_rejected_not_panicking() {
        // 64 *bytes*, 62 chars: the byte-length check passes, and `€` straddles
        // a slice boundary. Must reject, not panic (review #110).
        let hostile = format!("a\u{20ac}{}", "a".repeat(60));
        assert_eq!(hostile.len(), 64);
        assert_eq!(Fingerprint::parse_hex(&hostile), None);
    }

    #[test]
    fn words_stable_and_grouped() {
        let fp = Fingerprint([7u8; 32]);
        let words = fp.words();
        // 8 groups (32 bytes / 4 per group), each 4 words joined by '-':
        // one word per byte, 4 bytes per group.
        assert_eq!(words.split(' ').count(), 8);
        assert!(words.split(' ').all(|g| g.split('-').count() == 4));
        assert_eq!(Fingerprint([7u8; 32]).words(), words);
        // Spot-check the mapping: byte 7 indexes WORDS[7] in every group.
        let first_group: Vec<&str> = words.split(' ').next().unwrap().split('-').collect();
        assert!(first_group.iter().all(|w| *w == WORDS[7]));
    }

    #[test]
    fn wordlist_clean_and_unique() {
        assert_eq!(WORDS.len(), 256);
        let mut seen = std::collections::HashSet::new();
        for word in WORDS {
            assert!(!word.is_empty(), "empty wordlist entry");
            assert!(
                word.chars().all(|c| c.is_ascii_lowercase()),
                "wordlist must be plain lowercase ascii for phone readout: {word}"
            );
            assert!(seen.insert(word), "duplicate wordlist entry: {word}");
        }
    }

    #[test]
    fn distinct_inputs_distinct_fingerprints() {
        assert_ne!(
            Fingerprint::of_der(b"cert-a"),
            Fingerprint::of_der(b"cert-b")
        );
    }
}
