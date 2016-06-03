package org.apache.maven.plugins.shade.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarOutputStream;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;
import org.codehaus.plexus.util.StringUtils;

/**
 * Created by knightliao on 16/5/27.
 */
public class ProfileDontIncludeResourceTransformer implements ResourceTransformer {

    String resource;

    List<String> resources;

    private Log log;

    public Log getLog() {
        if (this.log == null) {
            this.log = new SystemStreamLog();
        }

        return this.log;
    }

    public boolean canTransformResource(String r) {

        if (StringUtils.isNotEmpty(resource) && r.endsWith(resource)) {
            getLog().info(String.format("ignore %s", r));
            return true;
        }

        if (resources != null) {
            for (String resourceEnd : resources) {
                if (r.endsWith(resourceEnd)) {
                    getLog().info(String.format("ignore %s", r));
                    return true;
                }
            }
        }

        return false;
    }

    public void processResource(String resource, InputStream is, List<Relocator> relocators)
            throws IOException {
        // no op
    }

    public boolean hasTransformedResource() {
        return false;
    }

    public void modifyOutputStream(JarOutputStream os)
            throws IOException {
        // no op
    }

}
