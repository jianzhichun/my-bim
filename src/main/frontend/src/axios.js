import axios from "axios";
import {message} from 'antd';
import qs from 'qs';

const instance = axios.create({baseURL: '/api'});

instance.native = axios;

instance.interceptors.request.use(req => req, err => Promise.reject(err));
instance.interceptors.response.use(resp => {
    const rs = resp.data;
    if (rs.success) {
        return rs.content;
    } else {
        const msg = rs.errorMessages.join('; ');
        if (msg.indexOf('csrf-token.missing') > -1) {
            const config = resp.config;
            if (config.retried) {
                message.error('Csrf-token missing, you can refresh or contact us. 请刷新页面或者联系我们.');
            } else {
                config.retried = true;
                const backoff = new Promise(function (resolve) {
                    setTimeout(function () {
                        resolve()
                    }, 1)
                });
                config.data = qs.parse(config.data);
                return backoff.then(() => {
                    return instance(config)
                });
            }
        } else {
            message.error(msg);
            return Promise.reject(msg);
        }
    }
}, err => message.error(err) && Promise.reject(err));
export default instance;