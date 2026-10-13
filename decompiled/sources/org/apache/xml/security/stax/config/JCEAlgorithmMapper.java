package org.apache.xml.security.stax.config;

import java.util.List;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.configuration.AlgorithmType;
import org.apache.xml.security.configuration.JCEAlgorithmMappingsType;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/config/JCEAlgorithmMapper.class */
public final class JCEAlgorithmMapper extends JCEMapper {
    private JCEAlgorithmMapper() {
    }

    protected static synchronized void init(JCEAlgorithmMappingsType jceAlgorithmMappingsType) throws Exception {
        List<AlgorithmType> algorithms = jceAlgorithmMappingsType.getAlgorithm();
        for (AlgorithmType algorithmType : algorithms) {
            int keyLength = 0;
            if (algorithmType.getKeyLength() != null) {
                keyLength = algorithmType.getKeyLength().intValue();
            }
            int ivLength = 0;
            if (algorithmType.getIVLength() != null) {
                ivLength = algorithmType.getIVLength().intValue();
            }
            JCEMapper.Algorithm algorithm = new JCEMapper.Algorithm(algorithmType.getRequiredKey(), algorithmType.getJCEName(), algorithmType.getAlgorithmClass(), keyLength, ivLength, algorithmType.getJCEProvider());
            register(algorithmType.getURI(), algorithm);
        }
    }
}
