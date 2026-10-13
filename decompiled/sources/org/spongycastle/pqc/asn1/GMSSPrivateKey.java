package org.spongycastle.pqc.asn1;

import java.math.BigInteger;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.pqc.crypto.gmss.GMSSLeaf;
import org.spongycastle.pqc.crypto.gmss.GMSSParameters;
import org.spongycastle.pqc.crypto.gmss.GMSSRootCalc;
import org.spongycastle.pqc.crypto.gmss.GMSSRootSig;
import org.spongycastle.pqc.crypto.gmss.Treehash;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/asn1/GMSSPrivateKey.class */
public class GMSSPrivateKey extends ASN1Object {
    private ASN1Primitive primitive;

    private GMSSPrivateKey(ASN1Sequence mtsPrivateKey) {
        ASN1Sequence indexPart = (ASN1Sequence) mtsPrivateKey.getObjectAt(0);
        int[] index = new int[indexPart.size()];
        for (int i = 0; i < indexPart.size(); i++) {
            index[i] = checkBigIntegerInIntRange(indexPart.getObjectAt(i));
        }
        ASN1Sequence curSeedsPart = (ASN1Sequence) mtsPrivateKey.getObjectAt(1);
        byte[][] curSeeds = new byte[curSeedsPart.size()][];
        for (int i2 = 0; i2 < curSeeds.length; i2++) {
            curSeeds[i2] = ((DEROctetString) curSeedsPart.getObjectAt(i2)).getOctets();
        }
        ASN1Sequence nextNextSeedsPart = (ASN1Sequence) mtsPrivateKey.getObjectAt(2);
        byte[][] nextNextSeeds = new byte[nextNextSeedsPart.size()][];
        for (int i3 = 0; i3 < nextNextSeeds.length; i3++) {
            nextNextSeeds[i3] = ((DEROctetString) nextNextSeedsPart.getObjectAt(i3)).getOctets();
        }
        ASN1Sequence curAuthPart0 = (ASN1Sequence) mtsPrivateKey.getObjectAt(3);
        byte[][][] curAuth = new byte[curAuthPart0.size()][][];
        for (int i4 = 0; i4 < curAuth.length; i4++) {
            ASN1Sequence curAuthPart1 = (ASN1Sequence) curAuthPart0.getObjectAt(i4);
            curAuth[i4] = new byte[curAuthPart1.size()][];
            for (int j = 0; j < curAuth[i4].length; j++) {
                curAuth[i4][j] = ((DEROctetString) curAuthPart1.getObjectAt(j)).getOctets();
            }
        }
        ASN1Sequence nextAuthPart0 = (ASN1Sequence) mtsPrivateKey.getObjectAt(4);
        byte[][][] nextAuth = new byte[nextAuthPart0.size()][][];
        for (int i5 = 0; i5 < nextAuth.length; i5++) {
            ASN1Sequence nextAuthPart1 = (ASN1Sequence) nextAuthPart0.getObjectAt(i5);
            nextAuth[i5] = new byte[nextAuthPart1.size()][];
            for (int j2 = 0; j2 < nextAuth[i5].length; j2++) {
                nextAuth[i5][j2] = ((DEROctetString) nextAuthPart1.getObjectAt(j2)).getOctets();
            }
        }
        ASN1Sequence seqOfcurTreehash0 = (ASN1Sequence) mtsPrivateKey.getObjectAt(5);
        Treehash[][] treehashArr = new Treehash[seqOfcurTreehash0.size()][];
    }

    public GMSSPrivateKey(int[] index, byte[][] currentSeed, byte[][] nextNextSeed, byte[][][] currentAuthPath, byte[][][] nextAuthPath, Treehash[][] currentTreehash, Treehash[][] nextTreehash, Vector[] currentStack, Vector[] nextStack, Vector[][] currentRetain, Vector[][] nextRetain, byte[][][] keep, GMSSLeaf[] nextNextLeaf, GMSSLeaf[] upperLeaf, GMSSLeaf[] upperTreehashLeaf, int[] minTreehash, byte[][] nextRoot, GMSSRootCalc[] nextNextRoot, byte[][] currentRootSig, GMSSRootSig[] nextRootSig, GMSSParameters gmssParameterset, AlgorithmIdentifier digestAlg) {
        AlgorithmIdentifier[] names = {digestAlg};
        this.primitive = encode(index, currentSeed, nextNextSeed, currentAuthPath, nextAuthPath, keep, currentTreehash, nextTreehash, currentStack, nextStack, currentRetain, nextRetain, nextNextLeaf, upperLeaf, upperTreehashLeaf, minTreehash, nextRoot, nextNextRoot, currentRootSig, nextRootSig, gmssParameterset, names);
    }

    private ASN1Primitive encode(int[] index, byte[][] currentSeeds, byte[][] nextNextSeeds, byte[][][] currentAuthPaths, byte[][][] nextAuthPaths, byte[][][] keep, Treehash[][] currentTreehash, Treehash[][] nextTreehash, Vector[] currentStack, Vector[] nextStack, Vector[][] currentRetain, Vector[][] nextRetain, GMSSLeaf[] nextNextLeaf, GMSSLeaf[] upperLeaf, GMSSLeaf[] upperTreehashLeaf, int[] minTreehash, byte[][] nextRoot, GMSSRootCalc[] nextNextRoot, byte[][] currentRootSig, GMSSRootSig[] nextRootSig, GMSSParameters gmssParameterset, AlgorithmIdentifier[] algorithms) {
        ASN1EncodableVector result = new ASN1EncodableVector();
        ASN1EncodableVector indexPart = new ASN1EncodableVector();
        for (int i : index) {
            indexPart.add(new ASN1Integer(i));
        }
        result.add(new DERSequence(indexPart));
        ASN1EncodableVector curSeedsPart = new ASN1EncodableVector();
        for (byte[] bArr : currentSeeds) {
            curSeedsPart.add(new DEROctetString(bArr));
        }
        result.add(new DERSequence(curSeedsPart));
        ASN1EncodableVector nextNextSeedsPart = new ASN1EncodableVector();
        for (byte[] bArr2 : nextNextSeeds) {
            nextNextSeedsPart.add(new DEROctetString(bArr2));
        }
        result.add(new DERSequence(nextNextSeedsPart));
        ASN1EncodableVector curAuthPart0 = new ASN1EncodableVector();
        ASN1EncodableVector curAuthPart1 = new ASN1EncodableVector();
        for (int i2 = 0; i2 < currentAuthPaths.length; i2++) {
            for (int j = 0; j < currentAuthPaths[i2].length; j++) {
                curAuthPart0.add(new DEROctetString(currentAuthPaths[i2][j]));
            }
            curAuthPart1.add(new DERSequence(curAuthPart0));
            curAuthPart0 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(curAuthPart1));
        ASN1EncodableVector nextAuthPart0 = new ASN1EncodableVector();
        ASN1EncodableVector nextAuthPart1 = new ASN1EncodableVector();
        for (int i3 = 0; i3 < nextAuthPaths.length; i3++) {
            for (int j2 = 0; j2 < nextAuthPaths[i3].length; j2++) {
                nextAuthPart0.add(new DEROctetString(nextAuthPaths[i3][j2]));
            }
            nextAuthPart1.add(new DERSequence(nextAuthPart0));
            nextAuthPart0 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(nextAuthPart1));
        ASN1EncodableVector seqOfTreehash0 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfTreehash1 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfStat = new ASN1EncodableVector();
        ASN1EncodableVector seqOfByte = new ASN1EncodableVector();
        ASN1EncodableVector seqOfInt = new ASN1EncodableVector();
        for (int i4 = 0; i4 < currentTreehash.length; i4++) {
            for (int j3 = 0; j3 < currentTreehash[i4].length; j3++) {
                seqOfStat.add(new DERSequence(algorithms[0]));
                int tailLength = currentTreehash[i4][j3].getStatInt()[1];
                seqOfByte.add(new DEROctetString(currentTreehash[i4][j3].getStatByte()[0]));
                seqOfByte.add(new DEROctetString(currentTreehash[i4][j3].getStatByte()[1]));
                seqOfByte.add(new DEROctetString(currentTreehash[i4][j3].getStatByte()[2]));
                for (int k = 0; k < tailLength; k++) {
                    seqOfByte.add(new DEROctetString(currentTreehash[i4][j3].getStatByte()[3 + k]));
                }
                seqOfStat.add(new DERSequence(seqOfByte));
                seqOfByte = new ASN1EncodableVector();
                seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[0]));
                seqOfInt.add(new ASN1Integer(tailLength));
                seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[2]));
                seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[3]));
                seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[4]));
                seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[5]));
                for (int k2 = 0; k2 < tailLength; k2++) {
                    seqOfInt.add(new ASN1Integer(currentTreehash[i4][j3].getStatInt()[6 + k2]));
                }
                seqOfStat.add(new DERSequence(seqOfInt));
                seqOfInt = new ASN1EncodableVector();
                seqOfTreehash1.add(new DERSequence(seqOfStat));
                seqOfStat = new ASN1EncodableVector();
            }
            seqOfTreehash0.add(new DERSequence(seqOfTreehash1));
            seqOfTreehash1 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfTreehash0));
        ASN1EncodableVector seqOfTreehash2 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfTreehash3 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfStat2 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfByte2 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfInt2 = new ASN1EncodableVector();
        for (int i5 = 0; i5 < nextTreehash.length; i5++) {
            for (int j4 = 0; j4 < nextTreehash[i5].length; j4++) {
                seqOfStat2.add(new DERSequence(algorithms[0]));
                int tailLength2 = nextTreehash[i5][j4].getStatInt()[1];
                seqOfByte2.add(new DEROctetString(nextTreehash[i5][j4].getStatByte()[0]));
                seqOfByte2.add(new DEROctetString(nextTreehash[i5][j4].getStatByte()[1]));
                seqOfByte2.add(new DEROctetString(nextTreehash[i5][j4].getStatByte()[2]));
                for (int k3 = 0; k3 < tailLength2; k3++) {
                    seqOfByte2.add(new DEROctetString(nextTreehash[i5][j4].getStatByte()[3 + k3]));
                }
                seqOfStat2.add(new DERSequence(seqOfByte2));
                seqOfByte2 = new ASN1EncodableVector();
                seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[0]));
                seqOfInt2.add(new ASN1Integer(tailLength2));
                seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[2]));
                seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[3]));
                seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[4]));
                seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[5]));
                for (int k4 = 0; k4 < tailLength2; k4++) {
                    seqOfInt2.add(new ASN1Integer(nextTreehash[i5][j4].getStatInt()[6 + k4]));
                }
                seqOfStat2.add(new DERSequence(seqOfInt2));
                seqOfInt2 = new ASN1EncodableVector();
                seqOfTreehash3.add(new DERSequence(seqOfStat2));
                seqOfStat2 = new ASN1EncodableVector();
            }
            seqOfTreehash2.add(new DERSequence(new DERSequence(seqOfTreehash3)));
            seqOfTreehash3 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfTreehash2));
        ASN1EncodableVector keepPart0 = new ASN1EncodableVector();
        ASN1EncodableVector keepPart1 = new ASN1EncodableVector();
        for (int i6 = 0; i6 < keep.length; i6++) {
            for (int j5 = 0; j5 < keep[i6].length; j5++) {
                keepPart0.add(new DEROctetString(keep[i6][j5]));
            }
            keepPart1.add(new DERSequence(keepPart0));
            keepPart0 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(keepPart1));
        ASN1EncodableVector curStackPart0 = new ASN1EncodableVector();
        ASN1EncodableVector curStackPart1 = new ASN1EncodableVector();
        for (int i7 = 0; i7 < currentStack.length; i7++) {
            for (int j6 = 0; j6 < currentStack[i7].size(); j6++) {
                curStackPart0.add(new DEROctetString((byte[]) currentStack[i7].elementAt(j6)));
            }
            curStackPart1.add(new DERSequence(curStackPart0));
            curStackPart0 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(curStackPart1));
        ASN1EncodableVector nextStackPart0 = new ASN1EncodableVector();
        ASN1EncodableVector nextStackPart1 = new ASN1EncodableVector();
        for (int i8 = 0; i8 < nextStack.length; i8++) {
            for (int j7 = 0; j7 < nextStack[i8].size(); j7++) {
                nextStackPart0.add(new DEROctetString((byte[]) nextStack[i8].elementAt(j7)));
            }
            nextStackPart1.add(new DERSequence(nextStackPart0));
            nextStackPart0 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(nextStackPart1));
        ASN1EncodableVector currentRetainPart0 = new ASN1EncodableVector();
        ASN1EncodableVector currentRetainPart1 = new ASN1EncodableVector();
        ASN1EncodableVector currentRetainPart2 = new ASN1EncodableVector();
        for (int i9 = 0; i9 < currentRetain.length; i9++) {
            for (int j8 = 0; j8 < currentRetain[i9].length; j8++) {
                for (int k5 = 0; k5 < currentRetain[i9][j8].size(); k5++) {
                    currentRetainPart0.add(new DEROctetString((byte[]) currentRetain[i9][j8].elementAt(k5)));
                }
                currentRetainPart1.add(new DERSequence(currentRetainPart0));
                currentRetainPart0 = new ASN1EncodableVector();
            }
            currentRetainPart2.add(new DERSequence(currentRetainPart1));
            currentRetainPart1 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(currentRetainPart2));
        ASN1EncodableVector nextRetainPart0 = new ASN1EncodableVector();
        ASN1EncodableVector nextRetainPart1 = new ASN1EncodableVector();
        ASN1EncodableVector nextRetainPart2 = new ASN1EncodableVector();
        for (int i10 = 0; i10 < nextRetain.length; i10++) {
            for (int j9 = 0; j9 < nextRetain[i10].length; j9++) {
                for (int k6 = 0; k6 < nextRetain[i10][j9].size(); k6++) {
                    nextRetainPart0.add(new DEROctetString((byte[]) nextRetain[i10][j9].elementAt(k6)));
                }
                nextRetainPart1.add(new DERSequence(nextRetainPart0));
                nextRetainPart0 = new ASN1EncodableVector();
            }
            nextRetainPart2.add(new DERSequence(nextRetainPart1));
            nextRetainPart1 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(nextRetainPart2));
        ASN1EncodableVector seqOfLeaf = new ASN1EncodableVector();
        ASN1EncodableVector seqOfStat3 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfByte3 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfInt3 = new ASN1EncodableVector();
        for (int i11 = 0; i11 < nextNextLeaf.length; i11++) {
            seqOfStat3.add(new DERSequence(algorithms[0]));
            byte[][] tempByte = nextNextLeaf[i11].getStatByte();
            seqOfByte3.add(new DEROctetString(tempByte[0]));
            seqOfByte3.add(new DEROctetString(tempByte[1]));
            seqOfByte3.add(new DEROctetString(tempByte[2]));
            seqOfByte3.add(new DEROctetString(tempByte[3]));
            seqOfStat3.add(new DERSequence(seqOfByte3));
            seqOfByte3 = new ASN1EncodableVector();
            int[] tempInt = nextNextLeaf[i11].getStatInt();
            seqOfInt3.add(new ASN1Integer(tempInt[0]));
            seqOfInt3.add(new ASN1Integer(tempInt[1]));
            seqOfInt3.add(new ASN1Integer(tempInt[2]));
            seqOfInt3.add(new ASN1Integer(tempInt[3]));
            seqOfStat3.add(new DERSequence(seqOfInt3));
            seqOfInt3 = new ASN1EncodableVector();
            seqOfLeaf.add(new DERSequence(seqOfStat3));
            seqOfStat3 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfLeaf));
        ASN1EncodableVector seqOfUpperLeaf = new ASN1EncodableVector();
        ASN1EncodableVector seqOfStat4 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfByte4 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfInt4 = new ASN1EncodableVector();
        for (int i12 = 0; i12 < upperLeaf.length; i12++) {
            seqOfStat4.add(new DERSequence(algorithms[0]));
            byte[][] tempByte2 = upperLeaf[i12].getStatByte();
            seqOfByte4.add(new DEROctetString(tempByte2[0]));
            seqOfByte4.add(new DEROctetString(tempByte2[1]));
            seqOfByte4.add(new DEROctetString(tempByte2[2]));
            seqOfByte4.add(new DEROctetString(tempByte2[3]));
            seqOfStat4.add(new DERSequence(seqOfByte4));
            seqOfByte4 = new ASN1EncodableVector();
            int[] tempInt2 = upperLeaf[i12].getStatInt();
            seqOfInt4.add(new ASN1Integer(tempInt2[0]));
            seqOfInt4.add(new ASN1Integer(tempInt2[1]));
            seqOfInt4.add(new ASN1Integer(tempInt2[2]));
            seqOfInt4.add(new ASN1Integer(tempInt2[3]));
            seqOfStat4.add(new DERSequence(seqOfInt4));
            seqOfInt4 = new ASN1EncodableVector();
            seqOfUpperLeaf.add(new DERSequence(seqOfStat4));
            seqOfStat4 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfUpperLeaf));
        ASN1EncodableVector seqOfUpperTreehashLeaf = new ASN1EncodableVector();
        ASN1EncodableVector seqOfStat5 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfByte5 = new ASN1EncodableVector();
        ASN1EncodableVector seqOfInt5 = new ASN1EncodableVector();
        for (int i13 = 0; i13 < upperTreehashLeaf.length; i13++) {
            seqOfStat5.add(new DERSequence(algorithms[0]));
            byte[][] tempByte3 = upperTreehashLeaf[i13].getStatByte();
            seqOfByte5.add(new DEROctetString(tempByte3[0]));
            seqOfByte5.add(new DEROctetString(tempByte3[1]));
            seqOfByte5.add(new DEROctetString(tempByte3[2]));
            seqOfByte5.add(new DEROctetString(tempByte3[3]));
            seqOfStat5.add(new DERSequence(seqOfByte5));
            seqOfByte5 = new ASN1EncodableVector();
            int[] tempInt3 = upperTreehashLeaf[i13].getStatInt();
            seqOfInt5.add(new ASN1Integer(tempInt3[0]));
            seqOfInt5.add(new ASN1Integer(tempInt3[1]));
            seqOfInt5.add(new ASN1Integer(tempInt3[2]));
            seqOfInt5.add(new ASN1Integer(tempInt3[3]));
            seqOfStat5.add(new DERSequence(seqOfInt5));
            seqOfInt5 = new ASN1EncodableVector();
            seqOfUpperTreehashLeaf.add(new DERSequence(seqOfStat5));
            seqOfStat5 = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfUpperTreehashLeaf));
        ASN1EncodableVector minTreehashPart = new ASN1EncodableVector();
        for (int i14 : minTreehash) {
            minTreehashPart.add(new ASN1Integer(i14));
        }
        result.add(new DERSequence(minTreehashPart));
        ASN1EncodableVector nextRootPart = new ASN1EncodableVector();
        for (byte[] bArr3 : nextRoot) {
            nextRootPart.add(new DEROctetString(bArr3));
        }
        result.add(new DERSequence(nextRootPart));
        ASN1EncodableVector seqOfnextNextRoot = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnnRStats = new ASN1EncodableVector();
        new ASN1EncodableVector();
        ASN1EncodableVector seqOfnnRBytes = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnnRInts = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnnRTreehash = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnnRRetain = new ASN1EncodableVector();
        for (int i15 = 0; i15 < nextNextRoot.length; i15++) {
            seqOfnnRStats.add(new DERSequence(algorithms[0]));
            new ASN1EncodableVector();
            int heightOfTree = nextNextRoot[i15].getStatInt()[0];
            int tailLength3 = nextNextRoot[i15].getStatInt()[7];
            seqOfnnRBytes.add(new DEROctetString(nextNextRoot[i15].getStatByte()[0]));
            for (int j10 = 0; j10 < heightOfTree; j10++) {
                seqOfnnRBytes.add(new DEROctetString(nextNextRoot[i15].getStatByte()[1 + j10]));
            }
            for (int j11 = 0; j11 < tailLength3; j11++) {
                seqOfnnRBytes.add(new DEROctetString(nextNextRoot[i15].getStatByte()[1 + heightOfTree + j11]));
            }
            seqOfnnRStats.add(new DERSequence(seqOfnnRBytes));
            seqOfnnRBytes = new ASN1EncodableVector();
            seqOfnnRInts.add(new ASN1Integer(heightOfTree));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[1]));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[2]));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[3]));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[4]));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[5]));
            seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[6]));
            seqOfnnRInts.add(new ASN1Integer(tailLength3));
            for (int j12 = 0; j12 < heightOfTree; j12++) {
                seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[8 + j12]));
            }
            for (int j13 = 0; j13 < tailLength3; j13++) {
                seqOfnnRInts.add(new ASN1Integer(nextNextRoot[i15].getStatInt()[8 + heightOfTree + j13]));
            }
            seqOfnnRStats.add(new DERSequence(seqOfnnRInts));
            seqOfnnRInts = new ASN1EncodableVector();
            ASN1EncodableVector seqOfStat6 = new ASN1EncodableVector();
            ASN1EncodableVector seqOfByte6 = new ASN1EncodableVector();
            ASN1EncodableVector seqOfInt6 = new ASN1EncodableVector();
            if (nextNextRoot[i15].getTreehash() != null) {
                for (int j14 = 0; j14 < nextNextRoot[i15].getTreehash().length; j14++) {
                    seqOfStat6.add(new DERSequence(algorithms[0]));
                    int tailLength4 = nextNextRoot[i15].getTreehash()[j14].getStatInt()[1];
                    seqOfByte6.add(new DEROctetString(nextNextRoot[i15].getTreehash()[j14].getStatByte()[0]));
                    seqOfByte6.add(new DEROctetString(nextNextRoot[i15].getTreehash()[j14].getStatByte()[1]));
                    seqOfByte6.add(new DEROctetString(nextNextRoot[i15].getTreehash()[j14].getStatByte()[2]));
                    for (int k7 = 0; k7 < tailLength4; k7++) {
                        seqOfByte6.add(new DEROctetString(nextNextRoot[i15].getTreehash()[j14].getStatByte()[3 + k7]));
                    }
                    seqOfStat6.add(new DERSequence(seqOfByte6));
                    seqOfByte6 = new ASN1EncodableVector();
                    seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[0]));
                    seqOfInt6.add(new ASN1Integer(tailLength4));
                    seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[2]));
                    seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[3]));
                    seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[4]));
                    seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[5]));
                    for (int k8 = 0; k8 < tailLength4; k8++) {
                        seqOfInt6.add(new ASN1Integer(nextNextRoot[i15].getTreehash()[j14].getStatInt()[6 + k8]));
                    }
                    seqOfStat6.add(new DERSequence(seqOfInt6));
                    seqOfInt6 = new ASN1EncodableVector();
                    seqOfnnRTreehash.add(new DERSequence(seqOfStat6));
                    seqOfStat6 = new ASN1EncodableVector();
                }
            }
            seqOfnnRStats.add(new DERSequence(seqOfnnRTreehash));
            seqOfnnRTreehash = new ASN1EncodableVector();
            ASN1EncodableVector currentRetainPart3 = new ASN1EncodableVector();
            if (nextNextRoot[i15].getRetain() != null) {
                for (int j15 = 0; j15 < nextNextRoot[i15].getRetain().length; j15++) {
                    for (int k9 = 0; k9 < nextNextRoot[i15].getRetain()[j15].size(); k9++) {
                        currentRetainPart3.add(new DEROctetString((byte[]) nextNextRoot[i15].getRetain()[j15].elementAt(k9)));
                    }
                    seqOfnnRRetain.add(new DERSequence(currentRetainPart3));
                    currentRetainPart3 = new ASN1EncodableVector();
                }
            }
            seqOfnnRStats.add(new DERSequence(seqOfnnRRetain));
            seqOfnnRRetain = new ASN1EncodableVector();
            seqOfnextNextRoot.add(new DERSequence(seqOfnnRStats));
            seqOfnnRStats = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfnextNextRoot));
        ASN1EncodableVector curRootSigPart = new ASN1EncodableVector();
        for (byte[] bArr4 : currentRootSig) {
            curRootSigPart.add(new DEROctetString(bArr4));
        }
        result.add(new DERSequence(curRootSigPart));
        ASN1EncodableVector seqOfnextRootSigs = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnRSStats = new ASN1EncodableVector();
        new ASN1EncodableVector();
        ASN1EncodableVector seqOfnRSBytes = new ASN1EncodableVector();
        ASN1EncodableVector seqOfnRSInts = new ASN1EncodableVector();
        for (int i16 = 0; i16 < nextRootSig.length; i16++) {
            seqOfnRSStats.add(new DERSequence(algorithms[0]));
            new ASN1EncodableVector();
            seqOfnRSBytes.add(new DEROctetString(nextRootSig[i16].getStatByte()[0]));
            seqOfnRSBytes.add(new DEROctetString(nextRootSig[i16].getStatByte()[1]));
            seqOfnRSBytes.add(new DEROctetString(nextRootSig[i16].getStatByte()[2]));
            seqOfnRSBytes.add(new DEROctetString(nextRootSig[i16].getStatByte()[3]));
            seqOfnRSBytes.add(new DEROctetString(nextRootSig[i16].getStatByte()[4]));
            seqOfnRSStats.add(new DERSequence(seqOfnRSBytes));
            seqOfnRSBytes = new ASN1EncodableVector();
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[0]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[1]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[2]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[3]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[4]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[5]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[6]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[7]));
            seqOfnRSInts.add(new ASN1Integer(nextRootSig[i16].getStatInt()[8]));
            seqOfnRSStats.add(new DERSequence(seqOfnRSInts));
            seqOfnRSInts = new ASN1EncodableVector();
            seqOfnextRootSigs.add(new DERSequence(seqOfnRSStats));
            seqOfnRSStats = new ASN1EncodableVector();
        }
        result.add(new DERSequence(seqOfnextRootSigs));
        ASN1EncodableVector parSetPart0 = new ASN1EncodableVector();
        ASN1EncodableVector parSetPart1 = new ASN1EncodableVector();
        ASN1EncodableVector parSetPart2 = new ASN1EncodableVector();
        ASN1EncodableVector parSetPart3 = new ASN1EncodableVector();
        for (int i17 = 0; i17 < gmssParameterset.getHeightOfTrees().length; i17++) {
            parSetPart1.add(new ASN1Integer(gmssParameterset.getHeightOfTrees()[i17]));
            parSetPart2.add(new ASN1Integer(gmssParameterset.getWinternitzParameter()[i17]));
            parSetPart3.add(new ASN1Integer(gmssParameterset.getK()[i17]));
        }
        parSetPart0.add(new ASN1Integer(gmssParameterset.getNumOfLayers()));
        parSetPart0.add(new DERSequence(parSetPart1));
        parSetPart0.add(new DERSequence(parSetPart2));
        parSetPart0.add(new DERSequence(parSetPart3));
        result.add(new DERSequence(parSetPart0));
        ASN1EncodableVector namesPart = new ASN1EncodableVector();
        for (AlgorithmIdentifier algorithmIdentifier : algorithms) {
            namesPart.add(algorithmIdentifier);
        }
        result.add(new DERSequence(namesPart));
        return new DERSequence(result);
    }

    private static int checkBigIntegerInIntRange(ASN1Encodable a) {
        BigInteger b = ((ASN1Integer) a).getValue();
        if (b.compareTo(BigInteger.valueOf(2147483647L)) > 0 || b.compareTo(BigInteger.valueOf(-2147483648L)) < 0) {
            throw new IllegalArgumentException("BigInteger not in Range: " + b.toString());
        }
        return b.intValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.primitive;
    }
}
