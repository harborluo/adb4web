
var stompClient = null;

var app = angular.module('app', []);
app.controller('MainController', function ($rootScope, $scope, $http) {

    $scope.data = {
        //连接状态
        connected: false,
        //消息
        message: '',
        rows: []
    };

    //连接
    $scope.connect = function () {
        var socket = new SockJS('/my-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            // 注册发送消息
            stompClient.subscribe('/topic/send', function (msg) {
                $scope.data.rows.push(JSON.parse(msg.body));
                $scope.data.connected = true;
                $scope.$apply();
            });
            // 注册推送时间回调
            stompClient.subscribe('/topic/callback', function (r) {
                $scope.data.time = '当前服务器时间：' + r.body;
                $scope.data.connected = true;
                $scope.$apply();
            });

            $scope.data.connected = true;
            $scope.$apply();
        });
    };

    $scope.disconnect = function () {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        $scope.data.connected = false;
    };

    $scope.send = function () {
        stompClient.send("/app/send", {}, JSON.stringify({
            'message': $scope.data.message
        }));
        $scope.data.message='';
    };
});