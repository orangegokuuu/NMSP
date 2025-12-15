function AddOnUtil() {
}

AddOnUtil.makeNull = function (value) {
if (value === null || value === undefined || value === "") {
        return null;
    }
    else {
        return value;
    }
};

function StringFilter() {
}

StringFilter.CONTAINS = 'contains'; // 1;
StringFilter.EQUALS = 'equals'; // 2;
StringFilter.NOT_EQUALS = 'notEquals'; // 3;

StringFilter.prototype.init = function(params) {
	this.filterParams = params;
	this.applyActive = params.apply === true;
	this.newRowsActionKeep = params.newRowsAction === 'keep';
	this.filterText = null;
	this.filterType = StringFilter.CONTAINS;
	this.createGui();
};
StringFilter.prototype.onNewRowsLoaded = function() {
	if (!this.newRowsActionKeep) {
		this.setType(StringFilter.CONTAINS);
		this.setFilter(null);
	}
};
StringFilter.prototype.afterGuiAttached = function() {
	this.eFilterTextField.focus();
};
StringFilter.prototype.doesFilterPass = function(params) {
	if (!this.filterText) {
		return true;
	}
	var value = this.filterParams.valueGetter(params.node);
	if (!value) {
		if (this.filterType === StringFilter.NOT_EQUALS) {
			// if there is no value, but the filter type was 'not equals',
			// then it should pass, as a missing value is not equal whatever
			// the user is filtering on
			return true;
		} else {
			// otherwise it's some type of comparison, to which empty value
			// will always fail
			return false;
		}
	}
	var valueLowerCase = value.toString().toLowerCase();
	switch (this.filterType) {
	case StringFilter.CONTAINS:
		return valueLowerCase.indexOf(this.filterText) >= 0;
	case StringFilter.EQUALS:
		return value.toString() === this.filterText;
	case StringFilter.NOT_EQUALS:
		return value.toString() != this.filterText;
	default:
		// should never happen
		console.warn('invalid filter type ' + this.filterType);
		return false;
	}
};
StringFilter.prototype.getGui = function() {
	return this.eGui;
};
StringFilter.prototype.isFilterActive = function() {
	return this.filterText !== null;
};
StringFilter.prototype.createTemplate = function() {
	return "<div>\n " +
	"<select class=\"ag-filter-select\" id=\"filterType\">\n" +
	"	<option value=\"contains" + "\">Contains</option>\n" +
	"	<option value=\"equals"+ "\">Equals</option>\n" +
	"	<option value=\"notEquals"+ "\">Not Equals</option>\n" +
	"</select>" +
	"</div>\n" +
	"<div>\n" +
	"	<input class=\"ag-filter-filter\" id=\"filterText\" type=\"text\" placeholder=\"" + 'Filter...'+ "\"/>\n" +
	"</div>\n" +
	"<div class=\"ag-filter-apply-panel\" id=\"applyPanel\">\n" +
	"   <button type=\"button\" id=\"applyButton\">"+'Apply'+ "</button>\n" +
	"</div>\n";
};
StringFilter.prototype.createGui = function() {
	this.eGui = document.createElement('div');
	this.eGui.innerHTML = this.createTemplate();
	this.eFilterTextField = this.eGui.querySelector("#filterText");
	this.eTypeSelect = this.eGui.querySelector("#filterType");
	this.eFilterTextField.addEventListener('change', this.onFilterChanged.bind(this));
    this.eTypeSelect.addEventListener("change", this.onTypeChanged.bind(this));
	this.setupApply();
};
StringFilter.prototype.setupApply = function() {
	var _this = this;
	if (this.applyActive) {
		this.eApplyButton = this.eGui.querySelector('#applyButton');
		this.eApplyButton.addEventListener('click', function() {
			_this.filterParams.filterChangedCallback();
		});
	} else {
		this.eGui.removeChild(this.eGui.querySelector('#applyPanel'));
	}
};
StringFilter.prototype.onTypeChanged = function() {
	this.filterType = this.eTypeSelect.value;
	this.filterChanged();
};
StringFilter.prototype.onFilterChanged = function() {	
	var filterText = AddOnUtil.makeNull(this.eFilterTextField.value);
	if (filterText && filterText.trim() === '') {
		filterText = null;
	}
	var newFilterText;
	if (filterText !== null && filterText !== undefined) {
		newFilterText = filterText;
	} else {
		newFilterText = null;
	}
	if (this.filterText !== newFilterText) {
		this.filterText = newFilterText;
		this.filterChanged();
	}
};
StringFilter.prototype.filterChanged = function() {
	this.filterParams.filterModifiedCallback();
	if (!this.applyActive) {
		this.filterParams.filterChangedCallback();
	}
};
StringFilter.prototype.setType = function(type) {
	this.filterType = type;
	this.eTypeSelect.value = type;
};
StringFilter.prototype.setFilter = function(filter) {
	filter = AddOnUtil.makeNull(filter);
	if (filter) {
		this.filterText = filter;
		this.eFilterTextField.value = filter;
	} else {
		this.filterText = null;
		this.eFilterTextField.value = null;
	}
};
StringFilter.prototype.getType = function() {
	return this.filterType;
};
StringFilter.prototype.getFilter = function() {
	return this.filterText;
};
StringFilter.prototype.getModel = function() {
	if (this.isFilterActive()) {
		return { type : this.filterType, filter : this.filterText };
	} else {
		return null;
	}
};
StringFilter.prototype.setModel = function(model) {
	if (model) {
		this.setType(model.type);
		this.setFilter(model.filter);
	} else {
		this.setFilter(null);
	}
};


function OptionsFilter() {
	
}

OptionsFilter.prototype.init = function(params) {
	this.filterParams = params;
	this.applyActive = params.apply === true;
	this.newRowsActionKeep = params.newRowsAction === 'keep';
	this.filterOptions = params.options;
	this.filterText = null;
	this.filterType = StringFilter.EQUALS;
	this.createGui();
};

OptionsFilter.prototype.onNewRowsLoaded = function() {
	if (!this.newRowsActionKeep) {
		this.setType(StringFilter.EQUALS);
		this.setFilter(null);
	}
};
OptionsFilter.prototype.afterGuiAttached = function() {
	this.eFilterTextField.focus();
};
OptionsFilter.prototype.doesFilterPass = function(params) {
	if (!this.filterText) {
		return true;
	}
	var value = this.filterParams.valueGetter(params.node);
	if (!value) {
		if (this.filterType === StringFilter.NOT_EQUALS) {
			// if there is no value, but the filter type was 'not equals',
			// then it should pass, as a missing value is not equal whatever
			// the user is filtering on
			return true;
		} else {
			// otherwise it's some type of comparison, to which empty value
			// will always fail
			return false;
		}
	}
	return value.toString() === this.filterText;
};

OptionsFilter.prototype.createTemplate = function() {
	var options;
	this.filterOptions.forEach( function(item) {
	  options += "	<option value=\""+item.value + "\">" + item.label + "</option>\n";
	});
	
	return "<div>\n " +
	"<select class=\"ag-filter-select\" id=\"filterType\">\n" +
	"	<option value=''></option>\n" +
	options +
	"</select>" +
	"</div>\n" +
	"<div class=\"ag-filter-apply-panel\" id=\"applyPanel\">\n" +
	"   <button type=\"button\" id=\"applyButton\">"+'Apply'+ "</button>\n" +
	"</div>\n";
};
OptionsFilter.prototype.createGui = function() {
	this.eGui = document.createElement('div');
	this.eGui.innerHTML = this.createTemplate();
	this.eFilterTextField = this.eGui.querySelector("#filterType");
    this.eFilterTextField.addEventListener("change", this.onSelectChanged.bind(this));
	this.setupApply();
};
OptionsFilter.prototype.getGui = function() {
	return this.eGui;
};
OptionsFilter.prototype.isFilterActive = function() {
	return this.filterText !== null;
};
OptionsFilter.prototype.setupApply = function() {
	var _this = this;
	if (this.applyActive) {
		this.eApplyButton = this.eGui.querySelector('#applyButton');
		this.eApplyButton.addEventListener('click', function() {
			_this.filterParams.filterChangedCallback();
		});
	} else {
		this.eGui.removeChild(this.eGui.querySelector('#applyPanel'));
	}
};
OptionsFilter.prototype.onSelectChanged = function() {
	this.filterText = AddOnUtil.makeNull(this.eFilterTextField.value); 
	this.filterChanged();
};
OptionsFilter.prototype.filterChanged = function() {
	this.filterParams.filterModifiedCallback();
	if (!this.applyActive) {
		this.filterParams.filterChangedCallback();
	}
};
OptionsFilter.prototype.onFilterChanged = function() {
	var filterText = AddOnUtil.makeNull(this.eFilterTextField.value);
	if (filterText && filterText.trim() === '') {
		filterText = null;
	}
	var newFilterText;
	if (filterText !== null && filterText !== undefined) {
		newFilterText = filterText;
	} else {
		newFilterText = null;
	}
	if (this.filterText !== newFilterText) {
		this.filterText = newFilterText;
		this.filterChanged();
	}
};
OptionsFilter.prototype.setType = function(type) {
	this.filterType = type;
	this.eTypeSelect.value = type;
};
OptionsFilter.prototype.setFilter = function(filter) {
	filter = AddOnUtil.makeNull(filter);
	if (filter) {
		this.filterText = filter;
		this.eFilterTextField.value = filter;
	} else {
		this.filterText = null;
		this.eFilterTextField.value = null;
	}
};
OptionsFilter.prototype.getType = function() {
	return this.filterType;
};
OptionsFilter.prototype.getFilter = function() {
	return this.filterText;
};
OptionsFilter.prototype.getModel = function() {
	if (this.isFilterActive()) {
		return { type : this.filterType, filter : this.filterText };
	} else {
		return null;
	}
};
OptionsFilter.prototype.setModel = function(model) {
	if (model) {
		this.setType(model.type);
		this.setFilter(model.filter);
	} else {
		this.setFilter(null);
	}
};


// boolean option filter

function BooleanOptionsFilter() {
	
}

BooleanOptionsFilter.prototype.init = function(params) {
	this.filterParams = params;
	this.applyActive = params.apply === true;
	this.newRowsActionKeep = params.newRowsAction === 'keep';
	this.filterOptions = params.options;
	this.filterText = null;
	this.filterType = StringFilter.EQUALS;
	this.createGui();
};

BooleanOptionsFilter.prototype.onNewRowsLoaded = function() {
	if (!this.newRowsActionKeep) {
		this.setType(StringFilter.EQUALS);
		this.setFilter(null);
	}
};
BooleanOptionsFilter.prototype.afterGuiAttached = function() {
	this.eFilterTextField.focus();
};
BooleanOptionsFilter.prototype.doesFilterPass = function(params) {
	if (!this.filterText) {
		return true;
	}
	var value = this.filterParams.valueGetter(params.node);
	if (!value) {
		if (this.filterType === StringFilter.NOT_EQUALS) {
			// if there is no value, but the filter type was 'not equals',
			// then it should pass, as a missing value is not equal whatever
			// the user is filtering on
			return true;
		} else {
			// otherwise it's some type of comparison, to which empty value
			// will always fail
			return false;
		}
	}
	return value.toString() === this.filterText;
};

BooleanOptionsFilter.prototype.createTemplate = function() {
	var options;
	this.filterOptions.forEach( function(item) {
	  options += "	<option value=\""+item.value + "\">" + item.label + "</option>\n";
	});
	
	return "<div>\n " +
	"<select class=\"ag-filter-select\" id=\"filterType\">\n" +
	"	<option value=''></option>\n" +
	options +
	"</select>" +
	"</div>\n" +
	"<div class=\"ag-filter-apply-panel\" id=\"applyPanel\">\n" +
	"   <button type=\"button\" id=\"applyButton\">"+'Apply'+ "</button>\n" +
	"</div>\n";
};
BooleanOptionsFilter.prototype.createGui = function() {
	this.eGui = document.createElement('div');
	this.eGui.innerHTML = this.createTemplate();
	this.eFilterTextField = this.eGui.querySelector("#filterType");
    this.eFilterTextField.addEventListener("change", this.onSelectChanged.bind(this));
	this.setupApply();
};
BooleanOptionsFilter.prototype.getGui = function() {
	return this.eGui;
};
BooleanOptionsFilter.prototype.isFilterActive = function() {
	return this.filterText !== null;
};
BooleanOptionsFilter.prototype.setupApply = function() {
	var _this = this;
	if (this.applyActive) {
		this.eApplyButton = this.eGui.querySelector('#applyButton');
		this.eApplyButton.addEventListener('click', function() {
			_this.filterParams.filterChangedCallback();
		});
	} else {
		this.eGui.removeChild(this.eGui.querySelector('#applyPanel'));
	}
};
BooleanOptionsFilter.prototype.onSelectChanged = function() {
	this.filterText = AddOnUtil.makeNull(this.eFilterTextField.value); 
	if(this.filterText == 'true' || this.filterText == 'false'){		
		this.filterText = JSON.parse(this.filterText); // parse string to boolean
	}
	this.filterChanged();
};
BooleanOptionsFilter.prototype.filterChanged = function() {
	this.filterParams.filterModifiedCallback();
	if (!this.applyActive) {
		this.filterParams.filterChangedCallback();
	}
};
BooleanOptionsFilter.prototype.onFilterChanged = function() {
	var filterText = AddOnUtil.makeNull(this.eFilterTextField.value);
	if (filterText && filterText.trim() === '') {
		filterText = null;
	}
	var newFilterText;
	if (filterText !== null && filterText !== undefined) {
		newFilterText = filterText;
	} else {
		newFilterText = null;
	}
	if (this.filterText !== newFilterText) {
		this.filterText = newFilterText;
		this.filterChanged();
	}
};
BooleanOptionsFilter.prototype.setType = function(type) {
	this.filterType = type;
	this.eTypeSelect.value = type;
};
BooleanOptionsFilter.prototype.setFilter = function(filter) {
	filter = AddOnUtil.makeNull(filter);
	if (filter) {
		this.filterText = filter;
		this.eFilterTextField.value = filter;
	} else {
		this.filterText = null;
		this.eFilterTextField.value = null;
	}
};
BooleanOptionsFilter.prototype.getType = function() {
	return this.filterType;
};
BooleanOptionsFilter.prototype.getFilter = function() {
	return this.filterText;
};
BooleanOptionsFilter.prototype.getModel = function() {
	if (this.isFilterActive()) {
		return { type : this.filterType, filter : this.filterText };
	} else {
		return null;
	}
};
BooleanOptionsFilter.prototype.setModel = function(model) {
	if (model) {
		this.setType(model.type);
		this.setFilter(model.filter);
	} else {
		this.setFilter(null);
	}
};

