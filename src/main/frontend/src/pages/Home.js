import {Avatar, Dropdown, Menu, Spin, Layout, Row} from "antd";
import {Redirect, useHistory, Route, Link} from "react-router-dom";
import React, {useCallback, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import axios from "../axios";
import {
    LogoutOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    ProjectOutlined,
    UserOutlined,
    RedoOutlined
} from "@ant-design/icons";
import Projects from './Projects';
import Project from './Project';

const {Header, Sider, Content, Footer} = Layout;

const HomeLayout = ({self}) => {
    const {t} = useTranslation(),
        [collapsed, setCollapsed] = useState(false),
        toggle = useCallback(() => setCollapsed(c => !c), []),
        history = useHistory();
    return (<Layout style={{height: '100vh'}}>
        <Sider trigger={null} collapsible collapsed={collapsed}>
            <div className="logo"/>
            <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']}>
                <Menu.Item key="1" icon={<UserOutlined/>}>
                    <Link to="/bim/myProjects">{t('project.mine')}</Link>
                </Menu.Item>
                <Menu.Item key="2" icon={<ProjectOutlined/>}>
                    <Link to="/bim/allProjects">{t('project.all')}</Link>
                </Menu.Item>
            </Menu>
        </Sider>
        <Layout className="site-layout">
            <Header className="site-layout-background" style={{padding: 0}}>
                {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
                    className: 'layout-trigger',
                    onClick: toggle,
                })}
                <Dropdown className="user-avatar" overlay={
                    <Menu>
                        <Menu.Item onClick={() => axios.post('/user/logout').then(() => history.push('/login'))}>
                            <LogoutOutlined/>
                            {t('logout')}
                        </Menu.Item>
                        <Menu.Item onClick={() => history.push('/resetPassword')}>
                            <RedoOutlined/>
                            {t('resetPassword.reset')}
                        </Menu.Item>
                    </Menu>
                } placement="bottomRight">
                    <Avatar>{self ? self.username.substring(0, 2) : t('anonymous')}</Avatar>
                </Dropdown>
            </Header>
            <Content className="site-layout-background" style={{minHeight: '660px',}}>
                <Route path="/bim" exact={true}>
                    <Redirect to="/bim/myProjects"/>
                </Route>
                <Route path="/bim/myProjects" component={() => <Projects mine={true}/>}/>
                <Route path="/bim/allProjects" component={() => <Projects mine={false}/>}/>
                <Route path="/bim/project/:id" component={Project}/>
            </Content>
            <Footer style={{textAlign: 'center'}}>{t('footer')}</Footer>
        </Layout>
    </Layout>)
};

export default () => {
    const {t} = useTranslation(),
        [loading, setLoading] = useState(true),
        [self, setSelf] = useState(undefined);
    useEffect(() => {
        axios.get('/user/self').then(loginSelf => {
            if (loginSelf) {
                setSelf(() => loginSelf);
            }
            setLoading(() => false);
        }).catch(() => setLoading(() => false));
    }, []);
    return loading ? <Row align={"middle"} justify={"center"} style={{height: '100vh', width: '100%'}}>
        <Spin size={"large"} tip={t('loading')}/>
    </Row> : (self ? <HomeLayout self={self}/> : <Redirect to="/login"/>);
}