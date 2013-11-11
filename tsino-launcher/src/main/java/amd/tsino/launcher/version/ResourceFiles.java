package amd.tsino.launcher.version;

import amd.tsino.bootstrap.EtagDatabase;
import amd.tsino.launcher.LauncherConstants;
import amd.tsino.launcher.LauncherUtils;
import amd.tsino.launcher.download.DownloadJob;
import amd.tsino.launcher.download.Downloadable;
import net.minecraft.launcher.Launcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourceFiles implements ArtifactList {
    private final File resourcesFile = LauncherUtils.getFile(LauncherConstants.RESOURCES_XML);

    public DownloadJob downloadJob() {
        URL resourcesURL = LauncherUtils.getURL(LauncherConstants.RESOURCES_URL);
        return new DownloadJob(resourcesFile, resourcesURL);
    }

    private List<Downloadable> getList() throws Exception {
        List<Downloadable> result = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc;
        if (!resourcesFile.exists()) {
            downloadJob().run();
        }
        try (InputStream is = new FileInputStream(resourcesFile)) {
            doc = db.parse(is);
        }
        NodeList nodeLst = doc.getElementsByTagName("Contents");

        for (int i = 0; i < nodeLst.getLength(); i++) {
            Node node = nodeLst.item(i);
            if (node.getNodeType() == 1) {
                Element element = (Element) node;
                String key = element.getElementsByTagName("Key").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String etag = element.getElementsByTagName("ETag") != null ? element
                        .getElementsByTagName("ETag").item(0)
                        .getChildNodes().item(0).getNodeValue()
                        : "-";
                long size = Long.parseLong(element
                        .getElementsByTagName("Size").item(0)
                        .getChildNodes().item(0).getNodeValue());

                if (size > 0L) {
                    final File file = LauncherUtils.getFile("assets/" + key);
                    if (etag.length() > 1) {
                        etag = EtagDatabase.formatEtag(etag);
                        if ((file.isFile()) && (file.length() == size)) {
                            String localEtag = EtagDatabase.getInstance().getEtag(file);
                            if (localEtag.equals(etag)) continue;
                        }
                    }
                    final URL url = new URL(LauncherConstants.RESOURCES_URL + key);
                    result.add(new DownloadJob(file, url));
                }
            }
        }
        return result;
    }

    @Override
    public List<Downloadable> getArtifacts() {
        try {
            return getList();
        } catch (Exception e) {
            Launcher.getInstance().getLog().error(e);
        }
        return new ArrayList<>();
    }
}
