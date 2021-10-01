package org.apache.maven.plugins.shade.mojos;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.shade.mojo.ShadeMojo;
import org.apache.maven.project.MavenProject;

import com.knightliao.plugin.starter.shade.utils.MavenFileUtils;
import com.knightliao.plugin.starter.shade.utils.TarFileUtils;

/**
 *
 */
@Mojo(name = StarterShade.FORMAT, defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
@Execute(phase = LifecyclePhase.PACKAGE)
public class StarterShade extends BaseStarterMojo {

    public static final String FORMAT = "shade";

    @Override
    protected String getFormat() {
        return FORMAT;
    }

    @Parameter
    private File startShell;

    @Parameter
    private File stopShell;

    @Parameter
    private String folderName = "starter-run";

    @Parameter
    private boolean includeProfileResource = false;

    /**
     * @throws MojoExecutionException
     */
    public void execute()
            throws MojoExecutionException {

        Map context = getPluginContext();
        MavenProject mavenProject = (MavenProject) context.get("project");

        getLog().info(mavenProject.toString());

        //
        // start shell
        if (startShell != null) {
            MavenFileUtils.copyFile(mavenProject, folderName, startShell, "start.sh");
        } else {
            getLog().info("use default start shell");
            MavenFileUtils.copyFile(mavenProject,
                    folderName,
                    StarterShade.class.getResource("/META-INF/start.sh"),
                    "start.sh");
        }

        //
        // stop shell
        if (stopShell != null) {
            MavenFileUtils.copyFile(mavenProject, folderName, stopShell, "stop.sh");
        } else {
            getLog().info("use default stop shell");
            MavenFileUtils.copyFile(mavenProject,
                    folderName,
                    StarterShade.class.getResource("/META-INF/stop.sh"),
                    "stop.sh");
        }

        //
        // other resources
        List<Resource> resources = new ArrayList<Resource>();

        List<Profile> activeProfiles = mavenProject.getActiveProfiles();
        for (Profile profile : activeProfiles) {
            getLog().info(String.format("add resource for profile %s", profile));
            BuildBase buildBase = profile.getBuild();
            if (buildBase != null) {
                List<Resource> curResources = buildBase.getResources();
                resources.addAll(curResources);
            }
        }

        for (Resource resource : resources) {

            String directory = resource.getDirectory();
            if (new File(directory).isDirectory()) {
                Collection<File> files = FileUtils.listFilesAndDirs(new File(directory),
                        FileFilterUtils.trueFileFilter(),
                        FileFilterUtils.trueFileFilter());
                for (File file : files) {
                    getLog().info(String.format("add file:[%s] as conf", file.getAbsolutePath()));
                    MavenFileUtils.copyFile(mavenProject, folderName, file, file.getAbsolutePath().replace
                            (directory, ""));
                }
            }
        }

        super.execute();

        //
        // move target jar
        try {

            Field field = ShadeMojo.class.getDeclaredField("finalName");
            field.setAccessible(true);
            String finalFileName = (String) field.get(this);
            getLog().info(finalFileName);

            String dir = mavenProject.getBuild().getDirectory();

            finalFileName = finalFileName + "." + mavenProject.getArtifact().getArtifactHandler().getExtension();
            File finalFile = new File(dir, finalFileName);
            MavenFileUtils.copyFile(mavenProject, folderName, finalFile, finalFileName);
            getLog().info(String.format("mv %s to folder %s", finalFile, folderName));

        } catch (Exception e) {
            getLog().warn(e.toString(), e);
        }

        //
        // make a tar.gz
        //
        try {

            Field field = ShadeMojo.class.getDeclaredField("finalName");
            field.setAccessible(true);
            String finalFileName = (String) field.get(this);

            getLog().info(String.format("make %s", folderName + ".tar.gz"));
            String dir = mavenProject.getBuild().getDirectory();

            TarFileUtils
                    .createDirTarGz(dir + File.separator + folderName,
                            dir + File.separator + finalFileName + ".tar.gz");

        } catch (Exception e) {
            getLog().info(e.toString());
        }

    }

}
