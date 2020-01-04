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
// Utility
// =====================================================
const groupBy = (array, getKey) =>
    Array.from(
        array.reduce((map, cur, idx, src) => {
            const key = getKey(cur, idx, src);
            const list = map.get(key);
            if (list) list.push(cur);
            else map.set(key, [cur]);
            return map;
        }, new Map())
    );

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
		<el-checkbox label="Functional Interface"></el-checkbox>
		<el-checkbox label="Abstract Class"></el-checkbox>
		<el-checkbox label="Class"></el-checkbox>
		<el-checkbox label="Enum"></el-checkbox>
		<el-checkbox label="Annotation"></el-checkbox>
		<el-checkbox label="Exception"></el-checkbox>
	  </el-checkbox-group>
	  <el-input size="mini" clearable placeholder="Search" v-model="selectedName"></el-input>
	  <div id="AllTypes">
		<a class='type' :title="item.packageName" v-for='item in filteredItems' v-on:click='link(item)'>{{item.name}}</a>
	  </div>
    </div>`,
	data: function() {
		return {
			items: root,
			selectedName: "",
			selectedPackage: "",
			selectedModule: "",
			selectedType: ["Interface", "Functional Interface", "Abstract Class", "Class", "Enum", "Annotation", "Exception"]
		};
	},
	computed: {
		filteredItems() {
			return this.items.types.filter(item => {
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

