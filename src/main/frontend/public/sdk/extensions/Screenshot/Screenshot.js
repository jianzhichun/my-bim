class ScreenshotToolExtension extends Autodesk.Viewing.Extension {
    constructor(viewer, options) {
        super(viewer, options);
        this.button = null;
    }
    async load() {
        await this.viewer.loadExtension('Autodesk.Snapping');
        return true;
    }
    async unload() {
        if (this.subToolbar) {
            this.viewer.toolbar.removeControl(this.subToolbar);
            this.subToolbar = null;
        }
    }
    onToolbarCreated(toolbar) {
        const viewer = this.viewer,
            {t, screenShotCallBack} = this.options,
            button = new Autodesk.Viewing.UI.Button('screenshot-bg-button');
        button.onClick = function (e) {
            if (viewer.screenModeDelegate.getMode() !== Autodesk.Viewing.ScreenMode.kNormal) {
                viewer.screenModeDelegate.setMode(Autodesk.Viewing.ScreenMode.kNormal);
            }
            viewer.getScreenShot(viewer.container.clientWidth, viewer.container.clientHeight, screenShotCallBack);
        };
        button.addClass('screenshot-button');
        button.setToolTip(t('project.model.screenshot'));
        this.subToolbar = new Autodesk.Viewing.UI.ControlGroup('screenshot-custom-toolbar');
        this.subToolbar.addControl(button);
        toolbar.addControl(this.subToolbar);
    }
}
Autodesk.Viewing.theExtensionManager.registerExtension('ScreenshotToolExtension', ScreenshotToolExtension);