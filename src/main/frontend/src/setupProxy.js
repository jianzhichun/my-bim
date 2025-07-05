const {createProxyMiddleware} = require('http-proxy-middleware');

module.exports = app => {
    app.use(createProxyMiddleware(['/api', '/bim-project'], {target: 'http://localhost:8080/'}))
}