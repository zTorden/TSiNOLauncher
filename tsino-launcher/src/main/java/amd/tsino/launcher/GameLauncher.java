package amd.tsino.launcher;

import amd.tsino.launcher.version.Library;
import amd.tsino.launcher.version.MinecraftVersion;
import amd.tsino.launcher.version.VersionFiles;
import net.minecraft.launcher.Launcher;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;

import java.io.PrintStream;

public class GameLauncher {

    private static void addLogger(Project proj, PrintStream ps) {
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(ps);
        logger.setErrorPrintStream(ps);
        logger.setMessageOutputLevel(Project.MSG_INFO);
        proj.addBuildListener(logger);
    }

    private static void addClientArgs(Java java, MinecraftVersion version, String sessionID, String uniqueID) {
        String[] args = version.getMinecraftArguments().split(" ");
        for (String arg : args) {
            switch (arg) {
                case "${auth_player_name}":
                case "${auth_username}":
                    java.createArg().setValue(Launcher.getInstance().getSettings().getCredentials().getUser());
                    break;
                case "${auth_session}":
                case "${auth_access_token}":
                    java.createArg().setValue(sessionID);
                    break;
                case "${version_name}":
                case "${assets_index_name}":
                    java.createArg().setValue(version.getID());
                    break;
                case "${game_directory}":
                    java.createArg().setFile(LauncherUtils.getClientFile(""));
                    break;
                case "${assets_root}":
                case "${game_assets}":
                    java.createArg().setFile(LauncherUtils.getClientFile(LauncherConstants.RESOURCES_BASE));
                    break;
                case "${auth_uuid}":
                    java.createArg().setValue(uniqueID);
                    break;
		case "${user_properties}":
		    java.createArg().setValue("{}"); 
		    break;
		case "${user_type}":
		    java.createArg().setValue("legacy");
		    break;
                default:
                    java.createArg().setValue(arg);
                    break;
            }
        }
    }

    private static void addJvmArgs(Java java) {
        Commandline.Argument jvmArgs = java.createJvmarg();
        jvmArgs.setLine(Launcher.getInstance().getSettings().getJavaArgs());
    }

    public static void launchGame(VersionFiles version, String sessionID, String uniqueID) throws Exception {
        Project project = new Project();
        project.setBaseDir(LauncherUtils.getClientFile(""));
        project.init();

        addLogger(project, System.out);
        addLogger(project, Launcher.getInstance().getLog().getPS());

        project.fireBuildStarted();

        try {
            Java javaTask = new Java();
            javaTask.setNewenvironment(true);
            javaTask.setTaskName("client");
            javaTask.setProject(project);
            javaTask.setFork(true);
            javaTask.setFailonerror(true);
            javaTask.setClassname(version.getVersion().getMainClass());

            addJvmArgs(javaTask);
            addClientArgs(javaTask, version.getVersion(), sessionID, uniqueID);

            Path classPath = new Path(project);
            for (Library lib : version.getVersion().getLibraries()) {
                if (!lib.isNative()) {
                    Path libPath = new Path(project);
                    libPath.setPath(lib.getFile().getAbsolutePath());
                    classPath.append(libPath);
                }
            }
            Path jarPath = new Path(project);
            jarPath.setPath(LauncherUtils.getClientFile(version.getVersion().getVersionJar()).getAbsolutePath());
            classPath.append(jarPath);
            javaTask.setClasspath(classPath);

            Environment.Variable variable = new Environment.Variable();
            variable.setKey("java.library.path");
            variable.setValue(version.getNativesDir().getAbsolutePath());
            javaTask.addSysproperty(variable);

            javaTask.init();
            Launcher.getInstance().getLog().log("%n%s%n", javaTask.getCommandLine().toString());
            int ret = javaTask.executeJava();
            Launcher.getInstance().getLog().log("Return code: " + ret);
            if (ret != 0) {
                throw new BuildException("Return code != 0");
            }
            project.fireBuildFinished(null);
        } catch (BuildException e) {
            project.fireBuildFinished(e);
            throw new Exception("Launch failed.", e);
        }
    }
}
