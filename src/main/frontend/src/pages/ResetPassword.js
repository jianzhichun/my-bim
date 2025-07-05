import {useTranslation} from "react-i18next";
import {useHistory, useLocation} from "react-router-dom";
import qs from "qs";
import {Button, Form, Image, Input, message, Row} from "antd";
import axios from "../axios";
import React from "react";

export default () => {
    const {t} = useTranslation(),
        location = useLocation(),
        history = useHistory(),
        {sid, userId} = qs.parse(location.search, {ignoreQueryPrefix: true});
    return (
        <Row style={{height: '80vh', flexFlow: "column"}} justify="center" align="middle" gutter={16}>
            <Image src={`${process.env.PUBLIC_URL}/logo.png`} style={{marginBottom: '10px'}}/>
            <Form labelCol={{xs: {span: 24,}, sm: {span: 10,},}} name="resetPassword"
                  onFinish={values => {
                      axios.post(`/user/${userId ? userId : values.username}/${sid ? sid + '/' : ''}resetPassword`, null, {params: values})
                          .then(() => {
                              history.push('/login');
                              message.success(t('resetPassword.success'));
                          });
                  }}
            >
                {
                    !userId ?
                        <Form.Item
                            name="username"
                            label={t('resetPassword.username.value')}
                            rules={[
                                {
                                    required: true,
                                    message: t('resetPassword.username.required'),
                                },
                            ]}
                        >
                            <Input/>
                        </Form.Item> : ''
                }
                {
                    !userId ?
                        <Form.Item
                            name="oldPassword"
                            label={t('resetPassword.oldPassword.value')}
                            rules={[
                                {
                                    required: true,
                                    message: t('resetPassword.oldPassword.required'),
                                },
                            ]}
                            hasFeedback
                        >
                            <Input.Password/>
                        </Form.Item> : ''
                }
                <Form.Item
                    name="password"
                    label={t('resetPassword.password.value')}
                    rules={[
                        {
                            required: true,
                            message: t('resetPassword.password.required'),
                        },
                    ]}
                    hasFeedback
                >
                    <Input.Password/>
                </Form.Item>
                <Form.Item
                    name="confirmedPassword"
                    label={t('resetPassword.confirmedPassword.value')}
                    dependencies={['password']}
                    hasFeedback
                    rules={[
                        {
                            required: true,
                            message: t('resetPassword.confirmedPassword.required'),
                        },
                        ({getFieldValue}) => ({
                            validator(rule, value) {
                                if (!value || getFieldValue('password') === value) {
                                    return Promise.resolve();
                                }

                                return Promise.reject(t('register.confirmedPassword.valid'));
                            },
                        }),
                    ]}
                >
                    <Input.Password/>
                </Form.Item>
                <Form.Item wrapperCol={{xs: {span: 24, offset: 0,}, sm: {span: 16, offset: 8,},}}>
                    <Button type="primary" htmlType="submit">
                        {t('resetPassword.reset')}
                    </Button>
                </Form.Item>
            </Form>
        </Row>
    );
};
