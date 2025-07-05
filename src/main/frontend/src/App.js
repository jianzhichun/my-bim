import React, {useEffect, useState} from 'react';

import './App.css';
import LanguageDetector from 'i18next-browser-languagedetector';
import i18n from "i18next";
import enUsTrans from "./locales/en-us.json";
import zhCnTrans from "./locales/zh-cn.json";
import {initReactI18next} from 'react-i18next';
import {useTranslation} from 'react-i18next';
import {BrowserRouter, Route, Redirect, useLocation} from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ResetPassword from "./pages/ResetPassword";
import ForgotPassword from "./pages/ForgotPassword";
import Home from "./pages/Home";
import {CookiesProvider} from 'react-cookie';
import {ConfigProvider, Row, Spin} from 'antd';
import zh from 'antd/es/locale/zh_CN';
import en from 'antd/es/locale/en_US';
import axios from "./axios";
import BimViewer from "./components/BimViewer";
import qs from "qs";


i18n.use(LanguageDetector)
    .use(initReactI18next)
    .init({
        resources: {
            en: {translation: enUsTrans,},
            zh: {translation: zhCnTrans,},
        },
        fallbackLng: "en",
        debug: false,
        interpolation: {escapeValue: false,},
    }).then(r => console.log(r));

export default () => {
    const {t, i18n} = useTranslation(),
        [antdLocale] = useState(i18n.languages[0] === 'en' ? en : zh);
    useEffect(() => {
        document.title = t('title');
    }, [t]);
    return (<CookiesProvider>
        <ConfigProvider locale={antdLocale}>
            <BrowserRouter>
                <Route exact path="/">
                    <Redirect to="/bim"/>
                </Route>
                <Route path="/bim-viewer/:id" component={({match}) => {
                    const id = match.params.id,
                        [models, setModels] = useState([]),
                        location = useLocation(),
                        {shareToken} = qs.parse(location.search, {ignoreQueryPrefix: true});
                    useEffect(() => {
                        axios.get(`/bim-project/${id}`).then(({models, name}) => {
                            document.title = `${name}--${t('title')}`;
                            if (shareToken) {
                                setModels(models.map(m => {
                                    const index = m.mainPath.indexOf('/bim-project/');
                                    m.mainPath = `${m.mainPath.substring(0, index + 13)}share/${shareToken}/${m.mainPath.substring(index + 13)}`;
                                    return {...m};
                                }));
                            } else {
                                setModels(models);
                            }
                        });
                    }, [id, shareToken]);
                    return (models.length > 0 &&
                        <BimViewer style={{width: '100%', height: '100vh'}} models={models} />) ||
                        <Row align={"middle"} justify={"center"} style={{height: '100vh', width: '100%'}}>
                            <Spin size={"large"} tip={t('loading')}/>
                        </Row>;
                }}/>
                <Route path="/bim" component={Home}/>
                <Route path="/login" component={Login}/>
                <Route path="/register" component={Register}/>
                <Route path="/forgotPassword" component={ForgotPassword}/>
                <Route path="/resetPassword" component={ResetPassword}/>
            </BrowserRouter>
        </ConfigProvider>
    </CookiesProvider>);
};