package org.apache.xml.security.utils.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.resolver.implementations.ResolverFragment;
import org.apache.xml.security.utils.resolver.implementations.ResolverXPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/resolver/ResourceResolver.class */
public class ResourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ResourceResolver.class);
    private static final List<ResourceResolverSpi> resolverList = new CopyOnWriteArrayList();
    private static final AtomicBoolean defaultResolversAdded = new AtomicBoolean();

    public static void register(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        register((ResourceResolverSpi) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, ResourceResolver.class)), false);
    }

    public static void registerAtStart(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        register((ResourceResolverSpi) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, ResourceResolver.class)), true);
    }

    public static void register(ResourceResolverSpi resourceResolverSpi, boolean start) {
        JavaUtils.checkRegisterPermission();
        if (start) {
            resolverList.add(0, resourceResolverSpi);
        } else {
            resolverList.add(resourceResolverSpi);
        }
        LOG.debug("Registered resolver: {}", resourceResolverSpi.toString());
    }

    public static void registerClassNames(List<String> classNames) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        List<ResourceResolverSpi> resourceResolversToAdd = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            ResourceResolverSpi resourceResolverSpi = (ResourceResolverSpi) JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, ResourceResolver.class));
            resourceResolversToAdd.add(resourceResolverSpi);
        }
        resolverList.addAll(resourceResolversToAdd);
    }

    public static void registerDefaultResolvers() {
        if (defaultResolversAdded.compareAndSet(false, true)) {
            List<ResourceResolverSpi> resourceResolversToAdd = new ArrayList<>();
            resourceResolversToAdd.add(new ResolverFragment());
            resourceResolversToAdd.add(new ResolverXPointer());
            resolverList.addAll(resourceResolversToAdd);
        }
    }

    public static XMLSignatureInput resolve(ResourceResolverContext context) throws ResourceResolverException {
        for (ResourceResolverSpi resolver : resolverList) {
            LOG.debug("check resolvability by class {}", resolver.getClass().getName());
            if (resolver.engineCanResolveURI(context)) {
                return resolver.engineResolveURI(context);
            }
        }
        Object[] exArgs = new Object[2];
        exArgs[0] = context.uriToResolve != null ? context.uriToResolve : "null";
        exArgs[1] = context.baseUri;
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, context.uriToResolve, context.baseUri);
    }

    public static XMLSignatureInput resolve(List<ResourceResolverSpi> individualResolvers, ResourceResolverContext context) throws ResourceResolverException {
        LOG.debug("I was asked to create a ResourceResolver and got {}", Integer.valueOf(individualResolvers == null ? 0 : individualResolvers.size()));
        if (individualResolvers != null) {
            for (ResourceResolverSpi resolver : individualResolvers) {
                String currentClass = resolver.getClass().getName();
                LOG.debug("check resolvability by class {}", currentClass);
                if (resolver.engineCanResolveURI(context)) {
                    return resolver.engineResolveURI(context);
                }
            }
        }
        return resolve(context);
    }
}
