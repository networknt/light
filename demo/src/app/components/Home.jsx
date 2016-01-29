import React from 'react';
import RaisedButton from 'material-ui/lib/raised-button';
import WebAPIUtils from '../utils/WebAPIUtils';
import NewsRecentPost from './news/NewsRecentPost';
import BlogRecentPost from './blog/BlogRecentPost';
/*
    recent news post    | Permaculture -> Permaculture Nursery
    recent blog post    | Edible Forest Garden -> Dundalk
    recent forum post   | ...

    footer
 */


let Home = React.createClass({
    render: function() {
        return(
            <div className="blogRoot">
                <div className="leftColumn">
                    <NewsRecentPost history={this.props.history} />
                    <BlogRecentPost history={this.props.history} />
                </div>
                <div className="rightColumn">
                </div>
            </div>
        )
    }
});

module.exports = Home;
/*
 <p>
 <b>Permaculture</b> is a philosophy of working with, rather than against nature; of protracted & thoughtful observation rather than protracted & thoughtless labour; of looking at plants & animals in all their functions, rather than treating any area as a single-product system.
 </p>
 <p>
 <b>Forest garden</b> is a low-maintenance sustainable plant-based food production and agroforestry system based on woodland ecosystems, incorporating fruit and nut trees, shrubs, herbs, vines and perennial vegetables which have yields directly useful to humans. Making use of companion planting, these can be intermixed to grow in a succession of layers, to build a woodland habitat.
 </p>
 <p>
 <b>Permaculture Nursery</b>
 </p>
 <p>
 <b>Permaculture Produce</b>
 </p>

 */