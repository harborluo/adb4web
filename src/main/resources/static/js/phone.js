var stompClient = null;

var phone = angular.module('phone', []);
phone.controller('PhoneController', function ($rootScope, $scope, $http, $location, $interval) {

    var url = $location.path();

    var lastRefreshInSeconds = 0;

    $scope.device = {
        connected : false,
        serialNo : "EMPTY_SERIAL_NO",
        screenImage : "blank.png",
        zoomScale : 0.4,
        mouse: {
            x: 0,
            y: 0,
        },
        screen: {
            width: 0,
            height: 0
        }
    };

    $scope.deviceList = [];

    $scope.appList = [];

    $scope.getConnectedDevices = function(){
        $http.get(url+"/devices/list").success(function (response) {
            $scope.deviceList = response;

            if($scope.deviceList.length==1){
                $scope.captureScreen($scope.deviceList[0].serialNo,
                                     $scope.deviceList[0].screenWidth,
                                     $scope.deviceList[0].screenHeight);

                $scope.initAppList();
            }

        });
    };

    $scope.captureScreen = function(sn, scrWidth, scrHeight){

        var captureUrl = url + "/devices/capture/screen/" + sn;
        $http.get(captureUrl).success(function (response) {
          if(response.success=="true"){
              /*
              $scope.device.connected = true;
              $scope.device.serialNo = sn;
              $scope.device.screenImage = response.imagePath;
               */
              if(scrWidth){
                  $scope.device.screen.width = scrWidth *  $scope.device.zoomScale;
              }
              if(scrHeight){
                  $scope.device.screen.height = scrHeight * $scope.device.zoomScale;
              }

              lastRefreshInSeconds = 0;
          }else{
              $scope.device.connected = false;
          }
        });
    };

    $scope.tapScreen = function(e){

        var actualX = Math.round(e.offsetX / $scope.device.zoomScale);
        var actualY= Math.round(e.offsetY / $scope.device.zoomScale);

        var tapUrl = url + "/devices/tap/screen/"+$scope.device.serialNo+"/"+actualX+"/" + actualY;
        //alert(tapUrl);
        console.log("tab on screen, x = " + actualX + ", y = "+ actualY);

        $http.get(tapUrl).success(function (response) {
            if(response.success=="true"){
                $scope.device.screenImage = response.imagePath;
                lastRefreshInSeconds = 0;
            } else {
                $scope.device.connected = false;
            }
        });

    };

    $scope.refreshScreen = function(){

        if($scope.device.connected == true) {
            return;
        }
/*
        lastRefreshInSeconds = lastRefreshInSeconds + 1;

        if(lastRefreshInSeconds < 5 ){
            return;
        }
        console.log("Auto refresh screen...")
        $scope.captureScreen($scope.device.serialNo);
*/
        var socket = new SockJS('/my-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {

            //receive message from web socket
            stompClient.subscribe('/topic/captureScreen', function (msg) {
                console.log("receive screen images from web socket...");
               var jsonBody = JSON.parse(msg.body)
                $scope.device.screenImage = jsonBody.image;
                $scope.$apply();
            });

        });
        $scope.device.connected = true;
    };

    $scope.initAppList = function(){
        var listUrl = url + "/app/game/list";
        $http.get(listUrl).success(function (response) {
            $scope.appList = response;
        });
    };

    $scope.startApp = function(appName){
        var listUrl = url + "/app/game/start?appName="+appName;
        $http.post(listUrl).success(function (response) {
           // $scope.appList = response;
            console.log(response.message);
        });
    };

    //on load
    $scope.getConnectedDevices();
    $interval($scope.refreshScreen, 1000, -1);
});
