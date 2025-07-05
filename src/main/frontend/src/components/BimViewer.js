import React, {useEffect, useRef, useState} from "react";
import Script from 'react-load-script';
import {useTranslation} from "react-i18next";
import {Image, Modal} from "antd";

export default (
    {
        models, loaded, success, error, successAllModels, errorAllModels, init, partSelect, cameraMove, screenShot,
        style, className
    }
) => {
    const {t} = useTranslation(),
        viewerDiv = useRef(),
        {current: emit} = useRef({
            loaded,
            success,
            error,
            successAllModels,
            errorAllModels,
            init,
            partSelect,
            cameraMove,
            screenShot
        }),
        [jsLoaded, setJsLoaded] = useState(false);
    useEffect(() => {
        if (jsLoaded && viewerDiv.current && models.length > 0) {
            const {Autodesk, THREE} = window,
                loadOptions = {placementTransform: new THREE.Matrix4()},
                viewModels = {};
            let viewer;
            const timer = setTimeout(() => {
                Autodesk.Viewing.theExtensionManager.registerExternalExtension(
                    'ScreenshotToolExtension',
                    `${window.location.protocol}//${window.location.host}/sdk/extensions/Screenshot/Screenshot.js`
                );
                Autodesk.Viewing.Initializer({env: "Local", offline: "true", useADP: false}, () => {
                    viewer = new Autodesk.Viewing.GuiViewer3D(viewerDiv.current, loadOptions);
                    if (viewer.start() > 0) {
                        console.error('Failed to create a Viewer: WebGL not supported.');
                        return;
                    }
                    const loadModel = (model, index) => new Promise((resolve, reject) => {
                        const option = {...loadOptions, ...model.option};
                        const onSuccessCallback = m => {
                            emit.success && emit.success(m, viewer);
                            viewModels[m.id] = m;
                            const geometryLoadedEvent = event => {
                                emit.loaded && emit.loaded(model.name, event, viewer);
                                if (index === 0) {
                                    viewer.utilities.goHome();
                                    viewer.utilities.fitToView();
                                }
                                if (emit.screenShot) {
                                    viewer.loadExtension('ScreenshotToolExtension', {
                                        t,
                                        screenShotCallBack: emit.screenShot
                                    });
                                }
                                viewer.removeEventListener(Autodesk.Viewing.GEOMETRY_LOADED_EVENT, geometryLoadedEvent);
                                resolve(model);
                            };
                            viewer.addEventListener(Autodesk.Viewing.GEOMETRY_LOADED_EVENT, geometryLoadedEvent);
                        }, onErrorCallback = (errorCode, errorMessage, errorArgs) => {
                            console.error(`${model.name}: Load Model Error, errorCode:${errorCode}, errorMessage: ${errorMessage}, errorArgs: ${errorArgs}`);
                            emit.error && emit.error(model.name, errorCode)
                            reject({name: model.name, errorCode, errorMessage, errorArgs});
                        };
                        viewer.loadModel(`${window.location.protocol}//${window.location.host}${model.mainPath}`, {
                            ...option,
                            modelNameOverride: model.name
                        }, onSuccessCallback, onErrorCallback)
                        if (index === 0) {
                            viewer.start(`${window.location.protocol}//${window.location.host}${model.mainPath}`);
                        }
                    });
                    if (models.length > 0) {
                        loadModel(models[0], 0)
                            .then(() => Promise
                                .all(models.slice(1, models.length).map((model, index) => loadModel(model, index + 1)))
                                .then(() => emit.successAllModels && emit.successAllModels(viewModels, viewer))
                                .catch(reason => emit.errorAllModels && emit.errorAllModels(reason)))
                            .catch(reason => emit.errorAllModels && emit.errorAllModels(reason));
                    }
                    emit.init && emit.init(viewer);
                    viewer.addEventListener(
                        Autodesk.Viewing.AGGREGATE_SELECTION_CHANGED_EVENT,
                        event => {
                            emit.partSelect && emit.partSelect(event.selections, event, viewer.getAggregateSelection());
                        }
                    );
                    viewer.addEventListener(Autodesk.Viewing.CAMERA_CHANGE_EVENT, event => emit.cameraMove && emit.cameraMove(event));
                });
            }, 200);
            return () => {
                clearTimeout(timer);
                if (viewer) {
                    viewer.tearDown();
                    viewer.finish();
                }
                if (window.Autodesk) {
                    window.Autodesk.Viewing.shutdown();
                }
            };
        }
    }, [jsLoaded, models, emit, t]);
    return <>
        <link rel="stylesheet" type="text/css" href={`${process.env.PUBLIC_URL}/sdk/style.min.css`}/>
        <Script url={`${process.env.PUBLIC_URL}/sdk/viewer3D.min.js`} onLoad={() => setJsLoaded(true)}/>
        <div className={className} style={models && models.length > 0 ? style || {} : {height: '0px'}}>
            <div style={{position: 'relative', width: '100%', height: '100%'}} ref={viewerDiv}/>
        </div>
    </>;
}