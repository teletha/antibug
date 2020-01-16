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
		<v-select v-model="selectedModule" placeholder="Select Module" :options="items.modules"></v-select>
		<v-select v-model="selectedPackage" placeholder="Select Package" :options="items.packages"></v-select>

		<p-check color="primary" v-model="selectedType" value="Interface">Interface</p-check>
		<p-check color="primary" v-model="selectedType" value="Functional">Functional Interface</p-check>
		<p-check color="primary" v-model="selectedType" value="AbstractClass">Abstract Class</p-check>
		<p-check color="primary" v-model="selectedType" value="Class">Class</p-check>
		<p-check color="primary" v-model="selectedType" value="Enum">Enum</p-check>
		<p-check color="primary" v-model="selectedType" value="Annotation">Annotation</p-check>
		<p-check color="primary" v-model="selectedType" value="Exception">Exception</p-check>

		<input id="SearchByName" v-model="selectedName" placeholder="Search by Name">
		
		<div class="tree">
			<dl v-for="package in sortedItems">
				<dt @click="toggle(package)" v-show="filter(package.children).length">{{package.name}}</dt>
				<dd v-for="type in filter(package.children)" :class="type.type" v-show="expandAll || package.isOpen" @click="link(type)">{{type.name}}</dd>
			</dl>
		</div>
	</div>
  `,
  data: function() {
    return {
      items: root,
      sortedItems: this.sortAndGroup(root),
      selectedName: "",
      selectedPackage: null,
      selectedModule: null,
      selectedType: [],
      expandAll: false
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
      this.expandAll = this.selectedType.length !== 0 || this.selectedPackage !== null || this.selectedName !== "";

      return items.filter(item => {
        if (this.selectedType.length != 0 && !this.selectedType.includes(item.type)) {
          return false;
        }

        if (this.selectedPackage !== null && this.selectedPackage !== item.packageName) {
          return false;
        }

        if (this.selectedName !== "" && (item.packageName + "." + item.name).toLowerCase().indexOf(this.selectedName.toLowerCase()) === -1) {
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
