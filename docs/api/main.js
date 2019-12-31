new Vue({
  el: "#moduleList",
  data: {
    items: ["*"].concat(root.modules),
    selected: ""
  }
})

const packageList = new Vue({
  el: "#packageList",
  data: {
    items: ["*"].concat(root.packages),
    selected: ""
  }
})

new Vue({
  el: "#typeList",
  data: {
    items: root.types
  },
  computed: {
    filteredItems() {
      return this.items.filter(item => {
        if (packageList.selected === "" || packageList.selected === "*") {
          return true;
        } else {
          return item.packageName === packageList.selected;
        }
      })
    }
  }
})