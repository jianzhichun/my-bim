import {Button, Form, Image, Input, message, Row, Tooltip} from "antd";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";
import axios from "../axios";
import {QuestionCircleOutlined} from "@ant-design/icons";
import React from "react";

export default  () => {
    const [form] = Form.useForm(),
        {t} = useTranslation(),
        history = useHistory();
    return (
        <Row style={{height: '80vh', flexFlow: "column"}} justify="center" align="middle" gutter={16}>
            <Image src={`${process.env.PUBLIC_URL}/logo.png`} style={{marginBottom: '10px'}}/>
            <Form
                labelCol={{xs: {span: 24,}, sm: {span: 8,},}}
                form={form}
                name="register"
                onFinish={values => {
                    axios.post('/user/signUp', null, {params: values}).then(() => {
                        history.push('/login');
                        message.success(t('register.success'));
                    });
                }}
                scrollToFirstError
            >
                <Form.Item
                    name="username"
                    label={
                        <span>
                            {t('register.username.value')}&nbsp;
                            <Tooltip title={t('register.username.tooltip')}>
                                <QuestionCircleOutlined/>
                            </Tooltip>
                        </span>
                    }
                    rules={[
                        {
                            required: true,
                            message: t('register.username.required'),
                            whitespace: true,
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="email"
                    label={t('register.email.value')}
                    rules={[
                        {
                            type: 'email',
                            message: t('register.email.valid'),
                        },
                        {
                            required: true,
                            message: t('register.email.required'),
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="phone"
                    label={t('register.phone.value')}
                >
                    <Input style={{width: '100%',}}/>
                </Form.Item>
                <Form.Item
                    name="password"
                    label={t('register.password.value')}
                    rules={[
                        {
                            required: true,
                            message: t('register.password.required'),
                        },
                    ]}
                    hasFeedback
                >
                    <Input.Password/>
                </Form.Item>

                <Form.Item
                    name="confirmedPassword"
                    label={t('register.confirmedPassword.value')}
                    dependencies={['password']}
                    hasFeedback
                    rules={[
                        {
                            required: true,
                            message: t('register.confirmedPassword.required'),
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
                        {t('register.register')}
                    </Button>
                </Form.Item>
            </Form>
        </Row>
    );
};