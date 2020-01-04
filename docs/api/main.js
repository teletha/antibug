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
			selectedType: ["Interface", "Functional", "AbstractClass", "Class", "Enum", "Annotation", "Exception"]
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
			let map = new Map;
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
			if (!this.selectedType.includes(item.type)) {
				return false;
			}

			if (this.selectedPackage !== "" && this.selectedPackage !== item.packageName) {
				return false;
			}
			
			if (this.selectedName !== "" && item.name.toLowerCase().indexOf(this.selectedName.toLowerCase()) === -1) {
				return false;
			}
			return true;
		},
		renderTree: function(h, o) {
			return h("span", {class:o.data.type ? o.data.type : "package"}, [o.data.name]);
		},
		link: function(e) {
			if (!e.packageName) return; // ignore package node

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

