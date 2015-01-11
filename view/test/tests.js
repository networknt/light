/* jshint expr: true */
chai.should();

describe('Schema form', function() {

  describe('directive', function() {
    beforeEach(module('templates'));
    beforeEach(module('schemaForm'));
    beforeEach(module('schemaForm-strapselect'));
    beforeEach(
      //We don't need no sanitation. We don't need no though control.
      module(function($sceProvider) {
        $sceProvider.enabled(false);
      })
    );

    it('should return correct form type for format "strapselect"',function(){
      inject(function($compile,$rootScope, schemaForm){
        var string_schema = {
          type: "object",
          properties: {
            file: {
              type: "string",
            }
          }
        };

        var strapselect_schema = {
          type: "object",
          properties: {
            file: {
              type: "string",
              format: "strapselect"
            }
          }
        };

        schemaForm.defaults(string_schema).form[0].type.should.be.eq("text");
        schemaForm.defaults(strapselect_schema).form[0].type.should.be.eq("strapselect");
      });
    });
    it('should return correct form type for format "strapmultiselect"',function(){
      inject(function($compile,$rootScope, schemaForm){
        var string_schema = {
          type: "object",
          properties: {
            file: {
              type: "string",
            }
          }
        };

        var strapmultiselect_schema = {
          type: "object",
          properties: {
            file: {
              type: "array",
              format: "strapselect"
            }
          }
        };

        schemaForm.defaults(string_schema).form[0].type.should.be.eq("text");
        schemaForm.defaults(strapmultiselect_schema).form[0].type.should.be.eq("strapmultiselect");
      });
    });


  });
});
