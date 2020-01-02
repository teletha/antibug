// =====================================================
// Vue Extensions
// =====================================================
Vue.append = function(selecter, componentDefinition) {
	const component = Vue.extend(componentDefinition);

	document.querySelectorAll(selecter).forEach(e => {
		new component().$mount(
			e.lastElementChild
				? e.lastElementChild
				: e.appendChild(document.createElement("span"))
		);
	});
};

// =====================================================
// Define Components
// =====================================================

Vue.append("#typeNavigation", {
	template: `
    <div>
      <el-select size="mini" clearable v-model='selectedModule' placeholder='Select Package' no-data-text="No Module">
        <el-option v-for='i in items.modules' :key='i' :label='i' :value='i'/>
      </el-select>
      <el-select size="mini" clearable v-model='selectedPackage' placeholder='Select Package' no-data-text="No Package">
        <el-option v-for='i in items.packages' :key='i' :label='i' :value='i'/>
      </el-select>
      <a class='type' v-for='item in filteredItems' v-on:click='link(item)'>{{item.name}}</a>
    </div>`,
	data: function() {
		return {
			items: root,
			selectedPackage: "",
			selectedModule: ""
		};
	},
	computed: {
		filteredItems() {
			return this.items.types.filter(item => {
				if (this.selectedPackage === "") {
					return true;
				} else {
					return item.packageName === this.selectedPackage;
				}
			});
		}
	},
	methods: {
		link: function(e) {
			var path =
				"/types/" +
				(e.packageName ? e.packageName + "." : "") +
				e.name +
				".html";

			fetch(path)
				.then(function(response) {
					return response.text();
				})
				.then(function(html) {
					var start = html.indexOf("<article");
					var end = html.lastIndexOf("</article>") + 10;
					html = html.substring(start, end);

					var article = document.querySelector("article");
					article.outerHTML = html;
				});
		}
	}
});
