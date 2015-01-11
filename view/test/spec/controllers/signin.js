'use strict';

describe('Controller: signinCtrl', function () {

  // load the controller's module
  beforeEach(module('lightwebApp'));

  var signinCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
      signinCtrl = $controller('signinCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
