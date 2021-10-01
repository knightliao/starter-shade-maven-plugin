package org.apache.maven.plugins.shade.mojos;

import org.apache.maven.plugins.shade.mojo.ShadeMojo;
import org.apache.maven.project.MavenProject;

/**
 *
 */
public abstract class BaseStarterMojo extends ShadeMojo {

    protected abstract String getFormat();


}
