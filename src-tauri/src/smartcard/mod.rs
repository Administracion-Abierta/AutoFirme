//! PC/SC smartcard session layer for DNIe (issue P1.4a, Refs #98).
//!
//! Digest in, raw signature out — this module never sees documents, PINs, or
//! files. PIN handling lives in `pin.rs` (sibling #99); hot-plug events in
//! `watch.rs`.

pub mod error;
pub mod opensc;
pub mod pkcs11;
pub mod reader;
pub mod session;

pub use error::SmartcardError;
