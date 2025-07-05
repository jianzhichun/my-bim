import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

window.onerror = function (errorMessage, scriptURI, lineNo, columnNo, error) {
    console.log('errorMessage: ' + errorMessage);
    console.log('scriptURI: ' + scriptURI);
    console.log('lineNo: ' + lineNo);
    console.log('columnNo: ' + columnNo);
    console.log('error: ' + error);
};
ReactDOM.render(<App/>, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
