package de.fuchsteufels.confluence.imagegrid.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Scanned
public class ImageGridMacro implements Macro {

    private final BootstrapManager bootstrapManager;
    private final PageBuilderService pageBuilderService;
    private final SettingsManager settingsManager;

    @Autowired
    public ImageGridMacro(@ComponentImport PageBuilderService pageBuilderService, @ComponentImport BootstrapManager bootstrapManager, @ComponentImport SettingsManager settingsManager) {
        this.pageBuilderService = pageBuilderService;
        this.bootstrapManager = bootstrapManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public String execute(Map<String, String> map, String s, ConversionContext conversionContext) throws MacroExecutionException {
        pageBuilderService.assembler().resources().requireWebResource("de.fuchsteufels.confluence.imagegrid:imagegrid-resources");
        return generateGridHTML(conversionContext, map);
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() { return OutputType.BLOCK; }

    private String generateGridHTML(ConversionContext conversionContext, Map<String, String> map) {
        final String ctxPath = bootstrapManager.getWebAppContextPath();
        final String widthClass = "fgi-" + map.get("width");
        final String manualWidth = "style=\"width: " + map.get("manualwidth") + "px;\"";
        final StringBuilder sb = new StringBuilder();

        sb.append("<div id=\"ft-grid-wrapper\">")
            .append("<div id=\"ft-grid\">");

        for (Attachment atachm : conversionContext.getPageContext().getEntity().getAttachments()) {

            if (atachm.isDeleted()) {
                continue;
            }

            if (atachm.getMediaType().contains("image")) {
                sb.append("<div class=\"ft-grid-item ").append(widthClass).append("\" ").append(manualWidth).append(" >")
                    .append("<img")
                        .append(" class=\"confluence-embedded-image\"")
                        .append(" src=\"").append(ctxPath).append(atachm.getDownloadPath()).append("\"")
                        .append(" data-image-src=\"").append(ctxPath).append(atachm.getDownloadPath()).append("\"")
                        .append(" data-linked-resource-id=\"").append(atachm.getContentId().asLong()).append("\"")
                        .append(" data-linked-resource-type=\"attachment\"")
                        .append(" data-linked-resource-default-alias=\"").append(atachm.getFileName()).append("\"")
                        .append(" data-base-url=\"").append(settingsManager.getGlobalSettings().getBaseUrl()).append("\"")
                        .append(" data-linked-resource-content-type=\"").append(atachm.getMediaType()).append("\"")
                        .append(" data-linked-resource-container-id=\"").append(atachm.getContainer().getIdAsString()).append("\"")
                        .append(" >");
                sb.append("</div>");
            }
        }

        sb.append("</div>"); // grid
        sb.append("</div>"); // wrapper

        System.out.println(sb.toString());

        return sb.toString();
    }

}