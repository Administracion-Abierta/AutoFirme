package org.spongycastle.crypto.examples;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.spongycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.spongycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.spongycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.spongycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.spongycastle.crypto.agreement.jpake.JPAKERound3Payload;
import org.spongycastle.crypto.digests.SHA256Digest;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/examples/JPAKEExample.class */
public class JPAKEExample {
    public static void main(String[] args) throws CryptoException {
        JPAKEPrimeOrderGroup group = JPAKEPrimeOrderGroups.NIST_3072;
        BigInteger p = group.getP();
        BigInteger q = group.getQ();
        BigInteger g = group.getG();
        System.out.println("********* Initialization **********");
        System.out.println("Public parameters for the cyclic group:");
        System.out.println("p (" + p.bitLength() + " bits): " + p.toString(16));
        System.out.println("q (" + q.bitLength() + " bits): " + q.toString(16));
        System.out.println("g (" + p.bitLength() + " bits): " + g.toString(16));
        System.out.println("p mod q = " + p.mod(q).toString(16));
        System.out.println("g^{q} mod p = " + g.modPow(q, p).toString(16));
        System.out.println("");
        System.out.println("(Secret passwords used by Alice and Bob: \"password\" and \"password\")\n");
        Digest digest = new SHA256Digest();
        SecureRandom random = new SecureRandom();
        JPAKEParticipant alice = new JPAKEParticipant("alice", "password".toCharArray(), group, digest, random);
        JPAKEParticipant bob = new JPAKEParticipant("bob", "password".toCharArray(), group, digest, random);
        JPAKERound1Payload aliceRound1Payload = alice.createRound1PayloadToSend();
        JPAKERound1Payload bobRound1Payload = bob.createRound1PayloadToSend();
        System.out.println("************ Round 1 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("g^{x1}=" + aliceRound1Payload.getGx1().toString(16));
        System.out.println("g^{x2}=" + aliceRound1Payload.getGx2().toString(16));
        System.out.println("KP{x1}={" + aliceRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{" + aliceRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
        System.out.println("KP{x2}={" + aliceRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{" + aliceRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");
        System.out.println("");
        System.out.println("Bob sends to Alice: ");
        System.out.println("g^{x3}=" + bobRound1Payload.getGx1().toString(16));
        System.out.println("g^{x4}=" + bobRound1Payload.getGx2().toString(16));
        System.out.println("KP{x3}={" + bobRound1Payload.getKnowledgeProofForX1()[0].toString(16) + "};{" + bobRound1Payload.getKnowledgeProofForX1()[1].toString(16) + "}");
        System.out.println("KP{x4}={" + bobRound1Payload.getKnowledgeProofForX2()[0].toString(16) + "};{" + bobRound1Payload.getKnowledgeProofForX2()[1].toString(16) + "}");
        System.out.println("");
        alice.validateRound1PayloadReceived(bobRound1Payload);
        System.out.println("Alice checks g^{x4}!=1: OK");
        System.out.println("Alice checks KP{x3}: OK");
        System.out.println("Alice checks KP{x4}: OK");
        System.out.println("");
        bob.validateRound1PayloadReceived(aliceRound1Payload);
        System.out.println("Bob checks g^{x2}!=1: OK");
        System.out.println("Bob checks KP{x1},: OK");
        System.out.println("Bob checks KP{x2},: OK");
        System.out.println("");
        JPAKERound2Payload aliceRound2Payload = alice.createRound2PayloadToSend();
        JPAKERound2Payload bobRound2Payload = bob.createRound2PayloadToSend();
        System.out.println("************ Round 2 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("A=" + aliceRound2Payload.getA().toString(16));
        System.out.println("KP{x2*s}={" + aliceRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{" + aliceRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");
        System.out.println("");
        System.out.println("Bob sends to Alice");
        System.out.println("B=" + bobRound2Payload.getA().toString(16));
        System.out.println("KP{x4*s}={" + bobRound2Payload.getKnowledgeProofForX2s()[0].toString(16) + "},{" + bobRound2Payload.getKnowledgeProofForX2s()[1].toString(16) + "}");
        System.out.println("");
        alice.validateRound2PayloadReceived(bobRound2Payload);
        System.out.println("Alice checks KP{x4*s}: OK\n");
        bob.validateRound2PayloadReceived(aliceRound2Payload);
        System.out.println("Bob checks KP{x2*s}: OK\n");
        BigInteger aliceKeyingMaterial = alice.calculateKeyingMaterial();
        BigInteger bobKeyingMaterial = bob.calculateKeyingMaterial();
        System.out.println("********* After round 2 ***********");
        System.out.println("Alice computes key material \t K=" + aliceKeyingMaterial.toString(16));
        System.out.println("Bob computes key material \t K=" + bobKeyingMaterial.toString(16));
        System.out.println();
        deriveSessionKey(aliceKeyingMaterial);
        deriveSessionKey(bobKeyingMaterial);
        JPAKERound3Payload aliceRound3Payload = alice.createRound3PayloadToSend(aliceKeyingMaterial);
        JPAKERound3Payload bobRound3Payload = bob.createRound3PayloadToSend(bobKeyingMaterial);
        System.out.println("************ Round 3 **************");
        System.out.println("Alice sends to Bob: ");
        System.out.println("MacTag=" + aliceRound3Payload.getMacTag().toString(16));
        System.out.println("");
        System.out.println("Bob sends to Alice: ");
        System.out.println("MacTag=" + bobRound3Payload.getMacTag().toString(16));
        System.out.println("");
        alice.validateRound3PayloadReceived(bobRound3Payload, aliceKeyingMaterial);
        System.out.println("Alice checks MacTag: OK\n");
        bob.validateRound3PayloadReceived(aliceRound3Payload, bobKeyingMaterial);
        System.out.println("Bob checks MacTag: OK\n");
        System.out.println();
        System.out.println("MacTags validated, therefore the keying material matches.");
    }

    private static BigInteger deriveSessionKey(BigInteger keyingMaterial) {
        SHA256Digest digest = new SHA256Digest();
        byte[] keyByteArray = keyingMaterial.toByteArray();
        byte[] output = new byte[digest.getDigestSize()];
        digest.update(keyByteArray, 0, keyByteArray.length);
        digest.doFinal(output, 0);
        return new BigInteger(output);
    }
}
