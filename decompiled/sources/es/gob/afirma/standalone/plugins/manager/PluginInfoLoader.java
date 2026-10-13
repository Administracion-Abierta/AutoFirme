package es.gob.afirma.standalone.plugins.manager;

import com.ctc.wstx.cfg.XmlConsts;
import es.gob.afirma.standalone.plugins.GenericMenuOption;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginButton;
import es.gob.afirma.standalone.plugins.PluginCommand;
import es.gob.afirma.standalone.plugins.PluginInfo;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/manager/PluginInfoLoader.class */
public class PluginInfoLoader {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    static PluginInfo parseInfo(InputStreamReader inputStreamReader) throws PluginException {
        JsonReader jsonReaderCreateReader = Json.createReader(inputStreamReader);
        Throwable th = null;
        try {
            try {
                JsonObject object = jsonReaderCreateReader.readObject();
                PluginInfo infoObject = parseInfoObject(object);
                Permission[] permissionsObject = parsePermissionsObject(object);
                if (permissionsObject != null) {
                    infoObject.setPermissions(permissionsObject);
                }
                infoObject.setInlineProcessorClassname(parseInlineProcessor(object));
                infoObject.setMenu(parseMenuObject(object));
                infoObject.setButtons(parseButtonsObject(object));
                infoObject.setCommands(parseCommandsObject(object));
                if (jsonReaderCreateReader != null) {
                    if (0 != 0) {
                        try {
                            jsonReaderCreateReader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        jsonReaderCreateReader.close();
                    }
                }
                return infoObject;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (jsonReaderCreateReader != null) {
                if (th != null) {
                    try {
                        jsonReaderCreateReader.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    jsonReaderCreateReader.close();
                }
            }
            throw th4;
        }
    }

    private static PluginInfo parseInfoObject(JsonObject jsonObject) throws PluginException {
        JsonObject jsonObject2 = jsonObject.getJsonObject("info");
        if (!jsonObject2.containsKey("name") || !jsonObject2.containsKey("title")) {
            throw new PluginException("El plugin no define la informacion basica necesaria ('name' y 'title')");
        }
        PluginInfo pluginInfo = new PluginInfo(jsonObject2.getString("name"), jsonObject2.getString("title"));
        pluginInfo.setVersionCode(jsonObject2.getInt("version_code", 1));
        pluginInfo.setVersion(jsonObject2.getString(XmlConsts.XML_DECL_KW_VERSION, "1.0"));
        pluginInfo.setDescription(jsonObject2.getString("description"));
        if (jsonObject2.containsKey("authors")) {
            JsonArray jsonArray = jsonObject2.getJsonArray("authors");
            String[] strArr = new String[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                strArr[i] = jsonArray.getString(i);
            }
            pluginInfo.setAuthors(strArr);
        }
        if (jsonObject2.containsKey("contacts")) {
            JsonArray jsonArray2 = jsonObject2.getJsonArray("contacts");
            String[] strArr2 = new String[jsonArray2.size()];
            for (int i2 = 0; i2 < jsonArray2.size(); i2++) {
                strArr2[i2] = jsonArray2.getString(i2);
            }
            pluginInfo.setContacts(strArr2);
        }
        if (jsonObject2.containsKey("configuration_panel")) {
            pluginInfo.setConfigPanel(jsonObject2.getString("configuration_panel"));
        }
        return pluginInfo;
    }

    private static Permission[] parsePermissionsObject(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.getJsonArray("permissions");
        if (jsonArray == null) {
            return new Permission[0];
        }
        HashSet hashSet = new HashSet();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonString jsonString = (JsonString) jsonArray.get(i);
            try {
                hashSet.add(Permission.forName(jsonString.getString()));
            } catch (Exception e) {
                LOGGER.warning("Permiso no reconocido: " + jsonString.getString());
            }
        }
        Permission[] permissionArr = (Permission[]) hashSet.toArray(new Permission[0]);
        Arrays.sort(permissionArr, (permission, permission2) -> {
            if (permission.getOrder() < permission2.getOrder()) {
                return -1;
            }
            return permission.getOrder() == permission2.getOrder() ? 0 : 1;
        });
        return permissionArr;
    }

    private static String parseInlineProcessor(JsonObject jsonObject) {
        JsonString jsonString = jsonObject.getJsonString("inline_processor");
        String string = null;
        if (jsonString != null && jsonString.getString() != null) {
            string = jsonString.getString();
        }
        return string;
    }

    private static GenericMenuOption parseMenuObject(JsonObject jsonObject) throws PluginException {
        JsonObject jsonObject2 = jsonObject.getJsonObject("menu");
        if (jsonObject2 == null) {
            return null;
        }
        if (!jsonObject2.containsKey("title") || (!jsonObject2.containsKey("items") && !jsonObject2.containsKey("action"))) {
            throw new PluginException("Se han encontrado menus del plugin mal definidos");
        }
        GenericMenuOption genericMenuOption = new GenericMenuOption(jsonObject2.getString("title"));
        if (jsonObject2.containsKey("action")) {
            genericMenuOption.setDialogClass(jsonObject2.getString("action"));
        } else {
            parseItemsObject(genericMenuOption, jsonObject2.getJsonArray("items"));
        }
        return genericMenuOption;
    }

    private static void parseItemsObject(GenericMenuOption genericMenuOption, JsonArray jsonArray) throws PluginException {
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                if (!jsonObject.containsKey("title") || (!jsonObject.containsKey("items") && !jsonObject.containsKey("action"))) {
                    throw new PluginException("Se han encontrado sub-menus del plugin mal definidos");
                }
                GenericMenuOption genericMenuOption2 = new GenericMenuOption(jsonObject.getString("title"));
                if (jsonObject.containsKey("items")) {
                    JsonArray jsonArray2 = jsonObject.getJsonArray("items");
                    if (jsonArray2 != null && jsonArray2.size() > 0) {
                        parseItemsObject(genericMenuOption2, jsonArray2);
                    }
                } else if (jsonObject.containsKey("action")) {
                    genericMenuOption2.setActionClassName(jsonObject.getJsonString("action").getString());
                }
                genericMenuOption.addSubMenu(genericMenuOption2);
            }
        }
    }

    private static PluginButton[] parseButtonsObject(JsonObject jsonObject) throws PluginException {
        JsonArray jsonArray = jsonObject.getJsonArray("buttons");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject) jsonArray.get(i);
            if (!jsonObject2.containsKey("title") && !jsonObject2.containsKey("icon")) {
                throw new PluginException("No se ha indicado el titulo ni el icono de un boton del plugin");
            }
            if (!jsonObject2.containsKey("action")) {
                throw new PluginException("No se ha definido una accion para un boton");
            }
            if (!jsonObject2.containsKey("window")) {
                throw new PluginException("No se ha definido la ventana para un boton");
            }
            PluginButton pluginButton = new PluginButton();
            pluginButton.setTitle(jsonObject2.getString("title", null));
            pluginButton.setIcon(jsonObject2.getString("icon", null));
            pluginButton.setToolTip(jsonObject2.getString("tooltip", null));
            pluginButton.setAccesibleDescription(jsonObject2.getString("accesible_description", null));
            pluginButton.setAfirmaWindow(jsonObject2.getString("window"));
            pluginButton.setActionClassName(jsonObject2.getString("action"));
            arrayList.add(pluginButton);
        }
        return (PluginButton[]) arrayList.toArray(new PluginButton[arrayList.size()]);
    }

    private static PluginCommand[] parseCommandsObject(JsonObject jsonObject) throws PluginException {
        JsonArray jsonArray = jsonObject.getJsonArray("commands");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject) jsonArray.get(i);
            if (!jsonObject2.containsKey("name")) {
                throw new PluginException("No se ha indicado el nombre de un comando del plugin");
            }
            if (!jsonObject2.containsKey("action")) {
                throw new PluginException("No se ha definido una clase de accion para un comando");
            }
            PluginCommand pluginCommand = new PluginCommand(jsonObject2.getString("name"));
            pluginCommand.setCommandActionClass(jsonObject2.getString("action"));
            pluginCommand.setDescription(jsonObject2.getString("description"));
            arrayList.add(pluginCommand);
        }
        return (PluginCommand[]) arrayList.toArray(new PluginCommand[arrayList.size()]);
    }
}
