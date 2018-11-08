;
(function () {
    if (window.WVJBridge) {
        return;
    }
    var responseCallbacks = {};
    var uniqueId = 1;
    var random = 1;

    function _doSend(message, responseCallback) {
        if (responseCallback) {
            var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            message['callbackId'] = callbackId;
        }
        var msg=JSON.stringify(message || {});
        window.WVJBInterface.call(msg);
    }

    var bridge = {
        callHandler: function (methodName, data, responseCallback) {
            if (arguments.length == 2 && typeof data == 'function') {
                responseCallback = data;
                data = null;
            }
            _doSend({
                methodName: methodName,
                data: data
            }, responseCallback);
        },
        _callback: function (messageJSON) {
            dispatchResponseFromNative(messageJSON);
        },
    };

    function dispatchResponseFromNative(message) {
        var messageHandler;
        var responseCallback;
        if (message.callbackId) {
            responseCallback = responseCallbacks[message.callbackId];
            if (!responseCallback) {
                return;
            }
            responseCallback(message.data);
            delete responseCallbacks[message.callbackId];
        }
    }

    var callbacks = window.WVJBCallbacks;
    delete window.WVJBCallbacks;
    if (callbacks) {
        for (var i = 0; i < callbacks.length; i++) {
            callbacks[i](bridge);
        }
    }
    window.WVJBridge = bridge;
})();	

(function(){
    if(window.bimaBridge){
        return;
    }

    function setupWVJBridge(callback) {
        if (window.WVJBridge) { return callback(WVJBridge); }
        if (window.WVJBCallbacks) { return window.WVJBCallbacks.push(callback); }
        window.WVJBCallbacks = [callback];
        var WVJBIframe = document.createElement('iframe');
        WVJBIframe.style.display = 'none';
        WVJBIframe.src = 'https://__bridge_loaded__';
        document.documentElement.appendChild(WVJBIframe);
        setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)
    }

    setupWVJBridge(function(bridge) {

        window.bimaBridge = {}
        window.bimaBridge.invoke = function(method,data,responseCallback) {
            bridge.callHandler(method,data,responseCallback);
        }
    })

})();
