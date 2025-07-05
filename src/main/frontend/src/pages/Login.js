import {useTranslation} from "react-i18next";
import {Link, useHistory} from "react-router-dom";
import {Button, Checkbox, Form, Image, Input, message, Row} from "antd";
import axios from "../axios";
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import React from "react";

export default () => {
    const {t} = useTranslation(),
        history = useHistory();
    return (
        <Row style={{height: '80vh', flexFlow: "column"}} justify="center" align="middle" gutter={16}>
            <Image src={`${process.env.PUBLIC_URL}/logo.png`} style={{marginBottom: '10px'}}/>
            <Form
                name="normal_login"
                className="login-form"
                initialValues={{
                    remember: true,
                }}
                onFinish={values =>
                    axios.post('/user/login', null, {params: values})
                        .then(() => {
                            history.push('/');
                            message.success(t('login.success'));
                        })
                }
            >
                <Form.Item
                    name="username"
                    rules={[
                        {
                            required: true,
                            message: t('login.username.required'),
                        },
                    ]}
                >
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           placeholder={t('login.username.value')}/>
                </Form.Item>
                <Form.Item
                    name="password"
                    rules={[
                        {
                            required: true,
                            message: t('login.password.required'),
                        },
                    ]}
                >
                    <Input
                        prefix={<LockOutlined className="site-form-item-icon"/>}
                        type="password"
                        placeholder={t('login.password.value')}
                    />
                </Form.Item>
                <Form.Item>
                    <Form.Item name="remember" valuePropName="checked" noStyle>
                        <Checkbox>{t('login.remember')}</Checkbox>
                    </Form.Item>

                    <Link className="login-form-forgot" to="/forgotPassword">
                        {t('login.forgotPassword')}
                    </Link>
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" className="login-form-button">
                        {t('login.login')}
                    </Button>
                    {t('login.or')} <Link to='/register'>{t('login.register')}</Link>
                </Form.Item>
            </Form>
        </Row>
    );
};