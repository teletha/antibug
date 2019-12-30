
new Vue({
  el: '#app',
  data: function() {
    return {
      location: [{id: 1, name: 'Site1', country: 'USA', metro: 'San Jose', market: 'US', status: 'Active'},
                  {id: 2, name: 'Site2', country: 'USA', metro: 'San Mateo', market: 'US', status: 'Active'},
                  {id: 3, name: 'Site3', country: 'USA', metro: 'San Rafael', market: 'US', status: 'Active'}]
    }
  }
})


new Vue({
  el: "#packageList",
  data: {
    packages: data.packages
  }
})

new Vue({
  el: "#typeList",
  data: {
    types: data.types
  }
})