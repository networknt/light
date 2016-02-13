import React from 'react';
import Menu from 'material-ui/lib/menus/menu';
import MenuItem from 'material-ui/lib/menus/menu-item';
import MenuStore from '../../stores/MenuStore';
import AuthStore from '../../stores/AuthStore';
import CommonUtils from '../../utils/CommonUtils';

class AdminMenu extends React.Component {

    constructor(props) {
        super(props);
        this._onItemTouchTap = this._onItemTouchTap.bind(this);
    }

    _onItemTouchTap(event, item) {
        console.log('AdminMenu props', this.props);
        console.log('AdminMenu._onItemTouchTap', item.props.value);
        this.context.router.push(item.props.value);
    }

    render() {
        let adminMenu = '';
        let out_Own = MenuStore.getMenu().out_Own;
        console.log('out_Own', out_Own);
        if (out_Own && CommonUtils.findMenuItem(out_Own, 'main')) {
            let mainMenuItems =  CommonUtils.findMenuItem(out_Own, 'main').out_Own;
            console.log('mainMenuItems', mainMenuItems);
            if(mainMenuItems && CommonUtils.findMenuItem(mainMenuItems, 'admin')) {
                let adminMenuItems = CommonUtils.findMenuItem(mainMenuItems, 'admin').out_Own;
                console.log('adminMenuItems', adminMenuItems);
                adminMenu = adminMenuItems.map((item, index) => {
                    if(CommonUtils.hasMenuAccess(item, AuthStore.getRoles())) {
                        return (
                            <MenuItem
                                key={index}
                                primaryText={item.text}
                                value={item.route}
                                />
                        );
                    }
                });
            }
        }

        return (
            <Menu onItemTouchTap={this._onItemTouchTap}>
                {adminMenu}
            </Menu>
        );
    }
}

AdminMenu.contextTypes = {
    router: React.PropTypes.object.isRequired
};

export default AdminMenu;
