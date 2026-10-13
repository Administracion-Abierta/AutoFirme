package org.bouncycastle.oer.its.template.etsi102941;

import com.ctc.wstx.cfg.XmlConsts;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.its.template.etsi102941.basetypes.EtsiTs102941BaseTypes;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.basetypes.Ieee1609Dot2BaseTypes;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/template/etsi102941/EtsiTs102941TrustLists.class */
public class EtsiTs102941TrustLists {
    public static final OERDefinition.Builder CrlEntry = Ieee1609Dot2BaseTypes.HashedId8.typeName("CrlEntry");
    public static final OERDefinition.Builder SequenceOfCrlEntry = OERDefinition.seqof(CrlEntry).typeName("SequenceOfCrlEntry");
    public static final OERDefinition.Builder ToBeSignedCrl = OERDefinition.seq(EtsiTs102941BaseTypes.Version.label(XmlConsts.XML_DECL_KW_VERSION), Ieee1609Dot2BaseTypes.Time32.label("thisUpdate"), Ieee1609Dot2BaseTypes.Time32.label("nextUpdate"), SequenceOfCrlEntry.label("entries"), OERDefinition.extension(new Object[0])).typeName("ToBeSignedCrl");
    public static final OERDefinition.Builder Url = OERDefinition.ia5String().typeName("Url");
    public static final OERDefinition.Builder DcDelete = Url.typeName("DcDelete");
    public static final OERDefinition.Builder DcEntry = OERDefinition.seq(Url.label("url"), Ieee1609Dot2BaseTypes.SequenceOfHashedId8.label("cert")).typeName("DcEntry");
    public static final OERDefinition.Builder AaEntry = OERDefinition.seq(EtsiTs103097Module.EtsiTs103097Certificate.label("aaCertificate"), Url.label("accessPoint")).typeName("AaEntry");
    public static final OERDefinition.Builder EaEntry = OERDefinition.seq(EtsiTs103097Module.EtsiTs103097Certificate.label("eaCertificate"), Url.label("aaAccessPoint"), OERDefinition.optional(Url.label("itsAccessPoint"))).typeName("EaEntry");
    public static final OERDefinition.Builder RootCaEntry = OERDefinition.seq(EtsiTs103097Module.EtsiTs103097Certificate.label("selfsignedRootCa"), OERDefinition.optional(EtsiTs103097Module.EtsiTs103097Certificate.label("successorTo"))).typeName("RootCaEntry");
    public static final OERDefinition.Builder TlmEntry = OERDefinition.seq(EtsiTs103097Module.EtsiTs103097Certificate.label("selfSignedTLMCertificate"), OERDefinition.optional(EtsiTs103097Module.EtsiTs103097Certificate.label("successorTo")), Url.label("accessPoint")).typeName("TlmEntry");
    public static final OERDefinition.Builder CtlDelete = OERDefinition.choice(Ieee1609Dot2BaseTypes.HashedId8.label("cert"), DcDelete.label("dc"), OERDefinition.extension(new Object[0])).typeName("CtlDelete");
    public static final OERDefinition.Builder CtlEntry = OERDefinition.choice(RootCaEntry.label("rca"), EaEntry.label("ea"), AaEntry.label("aa"), DcEntry.label("dc"), TlmEntry.label("tlm"), OERDefinition.extension(new Object[0])).typeName("CtlEntry");
    public static final OERDefinition.Builder CtlCommand = OERDefinition.choice(CtlEntry.label("add"), CtlDelete.label("delete"), OERDefinition.extension(new Object[0])).typeName("CtlCommand");
    public static final OERDefinition.Builder SequenceOfCtlCommand = OERDefinition.seqof(CtlCommand).typeName("SequenceOfCtlCommand");
    public static final OERDefinition.Builder CtlFormat = OERDefinition.seq(EtsiTs102941BaseTypes.Version.label(XmlConsts.XML_DECL_KW_VERSION), Ieee1609Dot2BaseTypes.Time32.label("nextUpdate"), OERDefinition.bool().label("isFullCtl"), OERDefinition.integer(0, 255).label("ctlSequence"), SequenceOfCtlCommand.label("ctlCommands"), OERDefinition.extension(new Object[0])).typeName("CtlFormat");
    public static final OERDefinition.Builder DeltaCtl = CtlFormat.typeName("DeltaCtl");
    public static final OERDefinition.Builder FullCtl = CtlFormat.typeName("FullCtl");
    public static final OERDefinition.Builder ToBeSignedTlmCtl = CtlFormat.typeName("ToBeSignedRcaCtl");
    public static final OERDefinition.Builder ToBeSignedRcaCtl = CtlFormat.typeName("ToBeSignedRcaCtl");
}
