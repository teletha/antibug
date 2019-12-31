
// =====================================================
// Vue Extensions
// =====================================================
Vue.append = function(selecter, componentDefinition) {
  const component = Vue.extend(componentDefinition);

  document.querySelectorAll(selecter).forEach(e => {
    new component().$mount(e.lastElementChild 
      ? e.lastElementChild
      : e.appendChild(document.createElement("span")));
  });
}

// =====================================================
// Define Components
// =====================================================



Vue.append("#typeNavigation",{
  template: `
    <div>
      <el-select v-model='selectedModule' placeholder='Select Package'>
        <el-option v-for='i in ["*"].concat(items.modules)' :key='i' :label='i' :value='i'/>
      </el-select>
      <el-select v-model='selectedPackage' placeholder='Select Package'>
        <el-option v-for='i in ["*"].concat(items.packages)' :key='i' :label='i' :value='i'/>
      </el-select>
      <a class='type' v-for='item in filteredItems'>{{item.name}}</a>
    </div>`,
  data: function() {
    return {
      items: root,
      selectedPackage: "",
      selectedModule: ""
    }
  },
  computed: {
    filteredItems() {
      return this.items.types.filter(item => {
        if (this.selectedPackage === "" || this.selectedPackage === "*") {
          return true;
        } else {
          return item.packageName === this.selectedPackage;
        }
      });
    }
  }
});