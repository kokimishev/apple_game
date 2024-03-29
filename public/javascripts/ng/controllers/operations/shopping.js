Game.controller('ShoppingController', [
    '$scope',
    '$translate',
    '$http',
    'Store',
    'StoreItems',
    'Operations',
    '$farmer',
    '$items',
    '$plantation',
    '$weather',
    function($scope, $translate, $http, Store, StoreItems, Operations, $farmer,
            $items, $plantation, $weather) {

      $plantation.load();

      var onBuyItem = function(item) {
        Store['buyItem']({
          itemid: item.id,
          quantity: item.perHa ? $scope.plantation.area : 1,
          currentState: $scope.$root.farmer.currentState
        }, null, function(result) {
          if (result.balans) {
            $items.add(item.store, item);
            $farmer.swap(result);
            $scope.$root.$emit('shop-hide');
          } else {
            $scope.$root.$emit('insuficient-funds');
          }
        }).$promise['finally'](function() {
          $scope.$root.$emit('item-bought');

        });
      };

      var showStoreItems = function(store) {
        $scope.$root.$emit('shop-hide');
        $scope.$root.$emit('item-bought');
        $scope.store = store;
        $scope.$root.$emit('shop-show', {
          items: StoreItems[store.name],
          showNext: true,
          storeUrl: store.url,
          onItemClick: onBuyItem

        });
      };

      var unreg = $scope.$root.$on('operation-store', function() {
        $scope.$root.$emit('shop-show', {
          items: StoreItems['stores'],
          showNext: true,
          storeUrl: '/public/images/game/operations/shop.png',
          onItemClick: showStoreItems
        });
      });

      $scope.$on("$destroy", function() {
        if (unreg) {
          unreg();
        }

      });

    }]);