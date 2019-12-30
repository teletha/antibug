var app = new Vue({
    el: '#app',
    data: {
      message: 'Hello Vue!'
    }
  })


var packageList = new Vue({
  el: "#packageList",
  data: {
    packages: data.packages
  }
})

var typeList = new Vue({
  el: "#typeList",
  data: {
    types: data.types
  }
})