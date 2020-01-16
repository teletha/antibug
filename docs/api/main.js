// =====================================================
// Define Router
// =====================================================
const router = new VueRouter({
  mode: "history",
  routes: [
    {
      path: "*",
      component: {
        template: "<i/>",
        created: function() {
          this.href();
        },
        watch: {
          $route: "href"
        },
        methods: {
          // ===========================================================
          // Extracts the contents and navigation from the HTML file at
          // the specified path and imports them into the current HTML.
          // ===========================================================
          href: function() {
            fetch(this.$route.params.pathMatch)
              .then(function(response) {
                return response.text();
              })
              .then(function(html) {
                var start = html.indexOf(">", html.indexOf("<article")) + 1;
                var end = html.lastIndexOf("</article>");
                var article = html.substring(start, end);
                document.querySelector("article").innerHTML = article;

                var start = html.indexOf(">", html.indexOf("<aside")) + 1;
                var end = html.lastIndexOf("</aside>");
                var aside = html.substring(start, end);
                document.querySelector("aside").innerHTML = aside;
              });
          }
        }
      }
    }
  ]
});

// =====================================================
// Define Components
// =====================================================
new Vue({
  el: "main",
  router
});

Vue.use(PrettyCheckbox);
Vue.component("v-select", VueSelect.VueSelect);

new Vue({
  el: "nav > div",
  template: `
	<div>
		<v-select v-model="selectedModule" :options="items.modules"></v-select>
		<v-select v-model="selectedPackage" :options="items.packages"></v-select>

		<p-check class="" color="primary" v-model="selectedType" value="Interface"><i slot="extra" class="icon mdi mdi-check"></i>Interface</p-check>
		<p-check color="primary" v-model="selectedType" value="Functional">Function Interface</p-check>
		<p-check color="primary" v-model="selectedType" value="AbstractClass">Abstract Class</p-check>
		<p-check color="primary" v-model="selectedType" value="Class">Class</p-check>
		<p-check color="primary" v-model="selectedType" value="Enum">Enum</p-check>
		<p-check color="primary" v-model="selectedType" value="Annotation">Annotation</p-check>
		<p-check color="primary" v-model="selectedType" value="Exception">Exception</p-check>

		<input v-model="selectedName" placeholder="Search by Name">
		
		<div v-for="package in sortedItems">
			<div @click="toggle(package)" v-show="filter(package.children).length">{{package.name}}</div>
			<div v-show="package.isOpen">
				<div v-for="type in filter(package.children)">
					<div @click="link(type)">{{type.name}}</div>
				</div>
			</div>
		</div>
	</div>
  `,
  data: function() {
    return {
      items: root,
      sortedItems: this.sortAndGroup(root),
      selectedName: "",
      selectedPackage: "",
      selectedModule: "",
      selectedType: []
    };
  },

  methods: {
    sortAndGroup: function(items) {
      let map = new Map();
      items.packages.forEach(item => {
        map.set(item, {
          name: item,
          children: [],
          isOpen: false
        });
      });

      items.types.forEach(item => {
        map.get(item.packageName).children.push(item);
      });

      return Array.from(map.values());
    },
    filter: function(items) {
      return items.filter(item => {
        if (
          this.selectedType.length != 0 &&
          !this.selectedType.includes(item.type)
        ) {
          return false;
        }

        if (
          this.selectedPackage !== null &&
          this.selectedPackage !== item.packageName
        ) {
          return false;
        }

        if (
          this.selectedName !== null &&
          item.name.toLowerCase().indexOf(this.selectedName.toLowerCase()) ===
            -1
        ) {
          return false;
        }
        return true;
      });
    },
    toggle: function(package) {
      package.isOpen = !package.isOpen;
    },
    link: function(type) {
      router.push("/types/" + type.packageName + "." + type.name + ".html");
    }
  }
});

/*
new Vue({
  el: "nav > div",
  template: `
	<div>
      <el-select size="mini" clearable v-model='selectedModule' placeholder='Select Module' no-data-text="No Module">
        <el-option v-for='i in items.modules' :key='i' :label='i' :value='i'/>
      </el-select>
      <el-select size="mini" clearable v-model='selectedPackage' placeholder='Select Package' no-data-text="No Package">
        <el-option v-for='i in items.packages' :key='i' :label='i' :value='i'/>
	  </el-select>
	  <el-checkbox-group v-model="selectedType">
		<el-checkbox label="Interface"></el-checkbox>
		<el-checkbox label="Functional"></el-checkbox>
		<el-checkbox label="AbstractClass" value="AbstractClass"></el-checkbox>
		<el-checkbox label="Class"></el-checkbox>
		<el-checkbox label="Enum"></el-checkbox>
		<el-checkbox label="Annotation"></el-checkbox>
		<el-checkbox label="Exception"></el-checkbox>
	  </el-checkbox-group>
	  <el-input size="mini" clearable placeholder="Search" v-model="selectedName"></el-input>
	  <el-tree id="AllTypes" empty-text="Not Found" :indent="0" :data="sortedItems" :render-content="renderTree" :props="{label:'name'}" :filter-node-method="filter" @node-click="link" ref="tree"></el-tree>
    </div>`,
  data: function() {
    return {
      items: root,
      sortedItems: this.sortAndGroup(root),
      selectedName: "",
      selectedPackage: "",
      selectedModule: "",
      selectedType: []
    };
  },
  watch: {
    selectedName(val) {
      this.$refs.tree.filter(val);
    },
    selectedType(val) {
      this.$refs.tree.filter(val);
    },
    selectedPackage(val) {
      this.$refs.tree.filter(val);
    },
    selectedModule(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    sortAndGroup: function(items) {
      let map = new Map();
      items.packages.forEach(item => {
        map.set(item, {
          name: item,
          children: []
        });
      });

      items.types.forEach(item => {
        map.get(item.packageName).children.push(item);
      });

      return Array.from(map.values());
    },
    filter: function(query, item) {
      if (
        this.selectedType.length != 0 &&
        !this.selectedType.includes(item.type)
      ) {
        return false;
      }

      if (
        this.selectedPackage !== "" &&
        this.selectedPackage !== item.packageName
      ) {
        return false;
      }

      if (
        this.selectedName !== "" &&
        item.name.toLowerCase().indexOf(this.selectedName.toLowerCase()) === -1
      ) {
        return false;
      }
      return true;
    },
    renderTree: function(h, o) {
      return h("span", { class: o.data.type ? o.data.type : "package" }, [
        o.data.name
      ]);
    },
    link: function(e) {
      if (!e.packageName) return; // ignore package node

      router.push("/types/" + e.packageName + "." + e.name + ".html");
    }
  }
});
*/

// =====================================================
// Global Event Listener
// =====================================================
document.addEventListener("click", event => {
  var e = event.target;

  if (e.tagName === "A") {
    var path = e.getAttribute("href");

    if (!path.startsWith("http") && !path.startsWith("#")) {
      // handle internal link only
      router.push(path);
      event.preventDefault();
    }
  }
});
