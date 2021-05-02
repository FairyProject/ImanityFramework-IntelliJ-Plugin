package org.imanity.framework.intellij.modules.template;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import org.imanity.framework.intellij.ImanityFrameworkAssets;
import org.imanity.framework.intellij.modules.FrameworkProjectSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class BukkitTemplate extends BaseTemplate {

    public static final BukkitTemplate INSTANCE = new BukkitTemplate();

    public static final String BUKKIT_MAIN_CLASS_TEMPLATE = "Bukkit Main Class.java",
            BUKKIT_MAIN_CLASS_KOTLIN_TEMPLATE = "Bukkit Main Class.kt",
            BUKKIT_POM_TEMPLATE = "Bukkit pom.xml",
            BUKKIT_BUILD_GRADLE_TEMPLATE = "Bukkit build.gradle",
            BUKKIT_GRADLE_PROPERTIES_TEMPLATE = "Bukkit gradle.properties",
            BUKKIT_SETTINGS_GRADLE_TEMPLATE = "Bukkit settings.gradle",
            BUKKIT_BUILD_GRADLE_KTS_TEMPLATE = "Bukkit build.gradle.kts",
            BUKKIT_SETTINGS_GRADLE_KTS_TEMPLATE = "Bukkit settings.gradle.kts";

    public String applyMainClass(Project project, String packageName, String className, FrameworkProjectSystem projectSystem, boolean kotlin) throws IOException {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("PACKAGE", packageName)
                .put("CLASS_NAME", className)
                .put("NAME", projectSystem.getName())
                .put("VERSION", projectSystem.getVersion())
                .put("DESCRIPTION", projectSystem.getDescription());

        // Depends
        if (projectSystem.getDependencies().length > 0 || projectSystem.getSoftDependencies().length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < projectSystem.getDependencies().length; i++) {
                String dependency = projectSystem.getDependencies()[i];

                stringBuilder.append("@PluginDependency(")
                        .append("\"")
                        .append(dependency)
                        .append("\"")
                        .append(")");
                if (i != projectSystem.getDependencies().length - 1 || projectSystem.getSoftDependencies().length > 0) {
                    stringBuilder.append(", ");
                }
            }

            for (int i = 0; i < projectSystem.getSoftDependencies().length; i++) {
                String dependency = projectSystem.getSoftDependencies()[i];

                stringBuilder.append("@PluginDependency(")
                        .append("\"")
                        .append(dependency)
                        .append("\"")
                        .append(", soft = true)");
                if (i != projectSystem.getSoftDependencies().length - 1) {
                    stringBuilder.append(", ");
                }
            }

            builder.put("DEPEND", stringBuilder.toString());
        }

        // Load Before
        if (projectSystem.getLoadBefore().length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < projectSystem.getLoadBefore().length; i++) {
                String dependency = projectSystem.getLoadBefore()[i];

                stringBuilder.append("\"").append(dependency).append("\"");
                if (i != projectSystem.getLoadBefore().length - 1) {
                    stringBuilder.append(", ");
                }
            }

            builder.put("LOAD_BEFORE", stringBuilder.toString());
        }

        // Authors
        if (projectSystem.getAuthors().length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < projectSystem.getAuthors().length; i++) {
                String dependency = projectSystem.getAuthors()[i];

                stringBuilder.append("\"").append(dependency).append("\"");
                if (i != projectSystem.getAuthors().length - 1) {
                    stringBuilder.append(", ");
                }
            }

            builder.put("AUTHOR", stringBuilder.toString());
        }

        builder.put("LOAD_ORDER", projectSystem.getLoadOrder());

        if (kotlin) {
            return this.applyTemplate(project, BUKKIT_MAIN_CLASS_KOTLIN_TEMPLATE, builder.build());
        }
        return this.applyTemplate(project, BUKKIT_MAIN_CLASS_TEMPLATE, builder.build());
    }

    public String applyPom(Project project) throws IOException {
        return this.applyTemplate(project, BUKKIT_POM_TEMPLATE, this.readMavenVersions());
    }

    public String applyGradleProperties(Project project) throws IOException {
        return this.applyTemplate(project, BUKKIT_GRADLE_PROPERTIES_TEMPLATE);
    }

    public String applySettingsGradle(Project project, FrameworkProjectSystem projectSystem) throws IOException {
        Map<String, String> properties = ImmutableMap.of("ARTIFACT_ID", projectSystem.getArtifactId());

        return this.applyTemplate(project, BUKKIT_SETTINGS_GRADLE_TEMPLATE, properties);
    }

    public String applySettingsGradleKts(Project project, FrameworkProjectSystem projectSystem) throws IOException {
        Map<String, String> properties = ImmutableMap.of("ARTIFACT_ID", projectSystem.getArtifactId());

        return this.applyTemplate(project, BUKKIT_SETTINGS_GRADLE_KTS_TEMPLATE, properties);
    }

    public String applyBuildGradle(Project project, FrameworkProjectSystem projectSystem) throws IOException {
        Map<String, String> map = ImmutableMap.of(
                "GROUP_ID", projectSystem.getGroupId(),
                "VERSION", projectSystem.getArtifactId()
        );

        return this.applyTemplate(project, BUKKIT_BUILD_GRADLE_TEMPLATE, map);
    }

    public String applyBuildGradleKts(Project project, FrameworkProjectSystem projectSystem) throws IOException {
        Map<String, String> map = ImmutableMap.of(
                "GROUP_ID", projectSystem.getGroupId(),
                "VERSION", projectSystem.getArtifactId()
        );

        return this.applyTemplate(project, BUKKIT_BUILD_GRADLE_KTS_TEMPLATE, map);
    }

    private Map<String, String> readMavenVersions() {
        // TODO - download from cloud
        final InputStream inputStream = ImanityFrameworkAssets.class.getResourceAsStream("/assets/maven.json");
        if (inputStream == null) {
            throw new IllegalArgumentException("The Assets /assets/maven.json is null");
        }

        return new Gson().fromJson(new InputStreamReader(inputStream), Map.class);
    }

}
