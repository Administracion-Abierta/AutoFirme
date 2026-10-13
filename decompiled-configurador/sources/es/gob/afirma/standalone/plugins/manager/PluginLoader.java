package es.gob.afirma.standalone.plugins.manager;

import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.GenericMenuOption;
import es.gob.afirma.standalone.plugins.PluginAction;
import es.gob.afirma.standalone.plugins.PluginButton;
import es.gob.afirma.standalone.plugins.PluginCommand;
import es.gob.afirma.standalone.plugins.PluginCommandAction;
import es.gob.afirma.standalone.plugins.PluginInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/plugins/manager/PluginLoader.class */
public class PluginLoader {
    private static final String CONFIG_FILE = "/plugin.json";
    public static final Map<String, AfirmaPlugin> classLoaderForPlugin = new HashMap();

    static AfirmaPlugin loadPlugin(File[] fileArr) throws IOException, PluginException {
        ArrayList arrayList = new ArrayList();
        for (File file : fileArr) {
            arrayList.add(file.toURI().toURL());
        }
        URLClassLoader uRLClassLoader = new URLClassLoader((URL[]) arrayList.toArray(new URL[arrayList.size()]), PluginLoader.class.getClassLoader());
        try {
            ArrayList arrayList2 = new ArrayList();
            try {
                Iterator it = ServiceLoader.load(AfirmaPlugin.class, uRLClassLoader).iterator();
                while (it.hasNext()) {
                    arrayList2.add((AfirmaPlugin) it.next());
                }
                if (arrayList2.size() == 0) {
                    uRLClassLoader.close();
                    throw new PluginException("No se encontro ningun plugin en los archivos");
                }
                if (arrayList2.size() > 1) {
                    uRLClassLoader.close();
                    throw new PluginException("No se permite la carga simulatea de varios plugins");
                }
                AfirmaPlugin afirmaPlugin = (AfirmaPlugin) arrayList2.get(0);
                afirmaPlugin.setClassLoader(uRLClassLoader);
                PluginInfo pluginInfoLoadPluginConfiguration = loadPluginConfiguration(afirmaPlugin);
                registryPluginReferences(afirmaPlugin, pluginInfoLoadPluginConfiguration);
                if (pluginInfoLoadPluginConfiguration.getButtons() != null) {
                    for (PluginButton pluginButton : pluginInfoLoadPluginConfiguration.getButtons()) {
                        if (pluginButton.getActionClassName() == null) {
                            uRLClassLoader.close();
                            throw new PluginException(String.format("El plugin '%1s' no ha definido accion para un boton", pluginInfoLoadPluginConfiguration.getName()));
                        }
                        if (pluginButton.getWindow() == null) {
                            uRLClassLoader.close();
                            throw new PluginException(String.format("El plugin '%1s' no ha definido la ventana en la que debe aparecer un boton", pluginInfoLoadPluginConfiguration.getName()));
                        }
                        try {
                            PluginAction pluginAction = (PluginAction) loadAction(pluginButton.getActionClassName(), PluginAction.class, uRLClassLoader);
                            pluginAction.setPlugin(afirmaPlugin);
                            pluginButton.setAction(pluginAction);
                        } catch (Exception e) {
                            uRLClassLoader.close();
                            throw new PluginException(String.format("El plugin '%1s' definio una clase de accion erronea: %2s", pluginInfoLoadPluginConfiguration.getName(), pluginButton.getActionClassName()), e);
                        }
                    }
                }
                afirmaPlugin.setInfo(pluginInfoLoadPluginConfiguration);
                return afirmaPlugin;
            } catch (Error e2) {
                uRLClassLoader.close();
                throw new PluginException("Se han contrado plugins mal definidos en el fichero importado");
            }
        } catch (Exception e3) {
            uRLClassLoader.close();
            throw new PluginException(String.format("Ha ocurrido un error al intentar cargar el plugin", new Object[0]), e3);
        }
    }

    private static void registryPluginReferences(AfirmaPlugin afirmaPlugin, PluginInfo pluginInfo) {
        ArrayList<String> arrayList = new ArrayList();
        getListActions(pluginInfo, arrayList);
        for (String str : arrayList) {
            if (!classLoaderForPlugin.containsKey(str)) {
                classLoaderForPlugin.put(str, afirmaPlugin);
            }
        }
    }

    private static void getListActions(PluginInfo pluginInfo, List<String> list) {
        if (pluginInfo != null) {
            if (pluginInfo.getButtons() != null) {
                for (PluginButton pluginButton : pluginInfo.getButtons()) {
                    list.add(pluginButton.getActionClassName());
                }
            }
            if (pluginInfo.getCommands() != null) {
                for (PluginCommand pluginCommand : pluginInfo.getCommands()) {
                    list.add(pluginCommand.getCommandActionClass());
                }
            }
            if (pluginInfo.getMenu() != null) {
                getListActionsFromMenus(pluginInfo.getMenu(), list);
            }
        }
    }

    private static void getListActionsFromMenus(GenericMenuOption genericMenuOption, List<String> list) {
        if (genericMenuOption.getActionClassName() != null) {
            list.add(genericMenuOption.getActionClassName());
        }
        if (genericMenuOption.getMenus() != null) {
            Iterator<GenericMenuOption> it = genericMenuOption.getMenus().iterator();
            while (it.hasNext()) {
                getListActionsFromMenus(it.next(), list);
            }
        }
    }

    /* JADX WARN: Not initialized variable reg: 12, insn: 0x00f1: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:43:0x00f1 */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x00f6: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:45:0x00f6 */
    private static PluginInfo loadPluginConfiguration(AfirmaPlugin afirmaPlugin) throws IOException, PluginException {
        InputStream inputStream;
        Throwable th;
        String str = '/' + afirmaPlugin.getClass().getPackage().getName().replace('.', '/') + CONFIG_FILE;
        try {
            try {
                InputStream resourceAsStream = afirmaPlugin.getClass().getResourceAsStream(str);
                Throwable th2 = null;
                try {
                    if (resourceAsStream == null) {
                        throw new IOException(String.format("No se encontro el fichero %1s en el plugin", str));
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
                    Throwable th3 = null;
                    try {
                        try {
                            PluginInfo info = PluginInfoLoader.parseInfo(inputStreamReader);
                            if (inputStreamReader != null) {
                                if (0 != 0) {
                                    try {
                                        inputStreamReader.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    inputStreamReader.close();
                                }
                            }
                            if (resourceAsStream != null) {
                                if (0 != 0) {
                                    try {
                                        resourceAsStream.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    resourceAsStream.close();
                                }
                            }
                            return info;
                        } catch (Throwable th6) {
                            th3 = th6;
                            throw th6;
                        }
                    } catch (Throwable th7) {
                        if (inputStreamReader != null) {
                            if (th3 != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable th8) {
                                    th3.addSuppressed(th8);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }
                        throw th7;
                    }
                } catch (Throwable th9) {
                    throw th9;
                }
            } catch (Throwable th10) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th11) {
                            th.addSuppressed(th11);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th10;
            }
        } catch (IOException e) {
            throw new IOException("Error en la lectura del fichero /plugin.json", e);
        } catch (Exception e2) {
            throw new PluginException("Se ha encontrado un error en el fichero /plugin.json", e2);
        }
    }

    public static PluginAction getPluginAction(String str) throws PluginException {
        AfirmaPlugin afirmaPlugin = classLoaderForPlugin.get(str);
        PluginAction pluginAction = (PluginAction) loadAction(str, PluginAction.class, afirmaPlugin.getClassLoader());
        pluginAction.setPlugin(afirmaPlugin);
        return pluginAction;
    }

    public static PluginCommandAction getPluginCommandAction(String str) throws PluginException {
        AfirmaPlugin afirmaPlugin = classLoaderForPlugin.get(str);
        PluginCommandAction pluginCommandAction = (PluginCommandAction) loadAction(str, PluginCommandAction.class, afirmaPlugin.getClassLoader());
        pluginCommandAction.setPlugin(afirmaPlugin);
        return pluginCommandAction;
    }

    private static Object loadAction(String str, Class<?> cls, ClassLoader classLoader) throws PluginException {
        try {
            Class<?> cls2 = Class.forName(str, false, classLoader);
            if (!cls.isAssignableFrom(cls2)) {
                throw new PluginException(String.format("Se ha establecido un boton que no define una accion de tipo %1s", PluginAction.class.getName()));
            }
            try {
                return cls2.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable th) {
                throw new PluginException(String.format("No se puede instanciar la clase %s. El constructor por defecto deber existir y ser publico", str), th);
            }
        } catch (Throwable th2) {
            throw new PluginException(String.format("La clase de accion %1s no existe", str), th2);
        }
    }
}
