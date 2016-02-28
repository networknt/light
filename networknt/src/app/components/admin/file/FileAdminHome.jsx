import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import FontIcon from 'material-ui/lib/font-icon';
import IconButton from 'material-ui/lib/icon-button';
import Dialog from 'material-ui/lib/dialog';
import CircularProgress from 'material-ui/lib/circular-progress';
import FileAdminStore from '../../../stores/FileAdminStore';
import FileActionCreators from '../../../actions/FileActionCreators';

var FileAdminHome = React.createClass({
    displayName: 'FileAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            paths: ['.'],
            files: []
        };
    },

    componentWillMount: function() {
        FileAdminStore.addChangeListener(this._onFileChange);
        FileActionCreators.getFile(this._getCurrentPath());
    },

    componentDidMount: function() {
        //console.log('this.uploadInput', this.uploadInput);
        this.uploadInput.addEventListener('change', this._onFileUpload(), false);
    },

    componentWillUnmount: function() {
        FileAdminStore.removeChangeListener(this._onFileChange);
    },


    _onFileChange: function() {
        let path = FileAdminStore.getPath();
        let files = FileAdminStore.getFiles(path);
        let paths = this.state.paths;
        if(paths[paths.length - 1] != path) {
            paths = paths.concat([path]);
        }
        //console.log('_onFileChange', files, paths);
        this.setState({
            files: files,
            paths: paths
        });
    },

    _onBackward: function() {
        if (this.state.paths.length < 2) {
            alert('Cannot go backward from ' + this._getCurrentPath());
            return;
        }
        this.state.paths = this.state.paths.slice(0,-1);
        FileActionCreators.getFile(this._getCurrentPath());
    },

    _onParent: function() {
        let path = this._getCurrentPath();
        if(path === '.') {
            alert('Cannot go to parent from .');
        } else {
            let newpath = '.';
            let slash = path.lastIndexOf('/');
            if(slash != -1) {
                newpath = path.substring(0, slash);
            }
            FileActionCreators.getFile(newpath);
        }
    },

    _onDelete: function() {
        if(typeof(this.selectIndex) != 'undefined') {
            let file = this.state.files[this.selectIndex];
            var type = file.isdir? 'folder' : 'file';
            var remove = confirm('Remove ' + type + ' ' + file.path + '?');
            if (remove) {
                FileActionCreators.delFile(this._getCurrentPath(), file);
                if(file.isdir === true) {
                    // remove the path from paths in states so that it won't nav back to it.
                    let filtered = this.state.paths.filter(e => e !== file.path);
                    this.setState({paths: filtered});
                }
            }
        } else {
            alert('Please select a file or folder.');
        }

    },

    _onRename: function() {
        if(typeof(this.selectIndex) != 'undefined') {
            let file = this.state.files[this.selectIndex];
            let oldName = file.name;
            let newName = prompt("Please enter a new name");
            if (newName === null) {
                return;
            } else {
                FileActionCreators.renFile(this._getCurrentPath(), oldName, newName);
            }
        } else {
            alert('Please select a file or folder.');
        }
    },

    _onNewFolder: function () {
        let path = this._getCurrentPath();
        var folder = prompt("Please enter a new folder name");
        if (folder == null) {
            return;
        } else {
            FileActionCreators.addFolder(path, folder);
        }
    },

    _onFileUpload: function() {
        // a callback from event listener.
        return function(e) {
            let file = e.target.files[0];
            let name = file.name;
            var reader = new FileReader();
            reader.onload = function(readerEvent) {
                var binaryString = readerEvent.target.result;
                var base64String = btoa(binaryString);
                FileActionCreators.uplFile(name, this._getCurrentPath(), base64String);
            }.bind(this);
            reader.readAsBinaryString(file);
        }.bind(this)
    },

    _onUpload: function() {
        if(this.uploadInput !== null) {
            this.uploadInput.click();
        }
    },

    _onDownload: function() {
        if(typeof(this.selectIndex) != 'undefined') {
            let file = this.state.files[this.selectIndex];
            if(file.isdir === true) {
                alert('Please select a file.');
            } else {
                FileActionCreators.getContent(file.path);
            }
        } else {
            alert('Please select a file.');
        }
    },

    _onOpenFolder: function() {
        if(typeof(this.selectIndex) != 'undefined') {
            let file = this.state.files[this.selectIndex];
            //console.log('file = ', file);
            if(file.isdir === true) {
                FileActionCreators.getFile(file.path);
            } else {
                alert('Please select a folder.');
            }
        } else {
            alert('Please select a folder.');
        }
    },

    _onRefresh: function() {
        FileActionCreators.getFile(this._getCurrentPath());
    },

    _onClick: function(file) {
        // open the folder or download the file
        if(file.isdir === true) {
            FileActionCreators.getFile(file.path);
        } else {
            FileActionCreators.getContent(file.path);
        }
    },

    _sortTime: function (left, right) { return left.time - right.time;},

    _sortSize: function(left, right){return left.size - right.size;},

    _sortPath: function(left, right){return left.path.localeCompare(right.path);},

    _onSortPath: function() {
        this._updateSort(this._sortPath);
    },

    _onSortSize: function() {
        this._updateSort(this._sortSize);
    },

    _onSortTime: function() {
        this._updateSort(this._sortTime)
    },

    _updateSort: function(sort) {
        let files = this.state.files;
        if(this.state.sort == sort) {
            files = files.reverse();
        } else {
            files = files.sort(sort);
        }
        this.setState({
            files: files,
            sort: sort
        })
    },

    _getSize: function(bytes) {
        let sizes = [{count : 1, unit:"bytes"}, {count : 1024, unit: "kB"}, {count: 1048576 , unit : "MB"}, {count: 1073741824, unit:"GB" } ];
        var count=0;
        for (var iUnit=0; iUnit < sizes.length; iUnit++) {
            count = bytes / sizes[iUnit].count;
            if (count < 1024)
                break;
        }
        return "" + (count|0) +" "+ sizes[iUnit].unit;
    },

    _getCurrentPath: function() {
        return this.state.paths[this.state.paths.length - 1];
    },

    _onRowSelection: function(selectedRows) {
        this.selectIndex = selectedRows[0];
        //console.log('select index', this.selectIndex);
    },

    render: function() {
        return (
            <span>
                <input type="file" id="uploadInput" ref={(ref) => this.uploadInput = ref}  style={{display:'none'}} />
                <Table
                    height={'1080px'}
                    fixedHeader={true}
                    fixedFooter={true}
                    selectable={true}
                    onRowSelection={this._onRowSelection}
                    multiSelectable={false}>
                    <TableHeader enableSelectAll={false}>
                        <TableRow>
                            <TableHeaderColumn colSpan="8" style={{textAlign: 'left'}}>
                                <IconButton iconClassName="material-icons" tooltip='Refresh' onTouchTap={this._onRefresh}>refresh</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Navigate backward' onTouchTap={this._onBackward}>arrow_back</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Parent folder' onTouchTap={this._onParent}>arrow_upward</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Upload file' onTouchTap={this._onUpload}>file_upload</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Download file. You can click the file' onTouchTap={this._onDownload}>file_download</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Open folder. You can click the folder' onTouchTap={this._onOpenFolder}>folder_open</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Create new folder' onTouchTap={this._onNewFolder}>create_new_folder</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Delete file or folder' onTouchTap={this._onDelete}>delete</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Rename' onTouchTap={this._onRename}>input</IconButton>
                                <IconButton iconClassName="material-icons" tooltip='Current path'>chevron_right</IconButton><b>{this._getCurrentPath()}</b>
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn colSpan="3">Path<IconButton iconClassName="material-icons" tooltip='Sort by path' onTouchTap={this._onSortPath}>import_export</IconButton></TableHeaderColumn>
                            <TableHeaderColumn>Size<IconButton iconClassName="material-icons" tooltip='Sort by size' onTouchTap={this._onSortSize}>import_export</IconButton></TableHeaderColumn>
                            <TableHeaderColumn colSpan="3">Last Modified<IconButton iconClassName="material-icons" tooltip='Sort by time' onTouchTap={this._onSortTime}>import_export</IconButton></TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.files.map((file, index) => {
                            let boundClick = this._onClick.bind(this, file);
                            let icon = 'description';
                            if(file.isdir) icon = 'folder';
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn colSpan="3"><a onClick={boundClick}><FontIcon className="material-icons">{icon}</FontIcon>{file.name}</a></TableRowColumn>
                                    <TableRowColumn>{this._getSize(file.size)}</TableRowColumn>
                                    <TableRowColumn colSpan="3">{new Date(file.time).toGMTString()}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>
                </Table>
            </span>
        );
    }
});

module.exports = FileAdminHome;
