import React from 'react';
import IconButton from 'material-ui/lib/icon-button';
import {darkWhite, lightWhite, grey900} from 'material-ui/lib/styles/colors';

const Footer = React.createClass({
    render() {
        return (
            <div classNames="container-fluid">
                <div className="footer">
                    <div className="footerFloat">
                        <ul>
                            <li><a href="#">About Us</a></li>
                            <li><a href="#">Terms and Conditions</a></li>
                            <li><a href="#">Privacy Policy</a></li>
                            <li><a href="#">Contact Us</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li><a href="#">Support</a></li>
                            <li><a href="#">Abuse Report</a></li>
                            <li><a href="#">FAQ</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li><a href="#">News</a></li>
                            <li><a href="#">Blog</a></li>
                            <li><a href="#">Forum</a></li>
                            <li><a href="#">Catalog</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li>
                            </li>
                            <li><a href="http://twitter.com/shopify" title="React - Responsive Shopify Theme - Graphite on Twitter" className="icon-social twitter ir">Twitter</a></li>
                            <li><a href="http://www.facebook.com/shopify" title="React - Responsive Shopify Theme - Graphite on Facebook" className="icon-social facebook ir">Facebook</a></li>
                            <li><a href="http://plus.google.com/+shopify" title="React - Responsive Shopify Theme - Graphite on Google+" className="icon-social google ir">Google+</a></li>
                            <li><a href="http://www.youtube.com/user/shopify" title="React - Responsive Shopify Theme - Graphite on YouTube" className="icon-social youtube ir">YouTube</a></li>
                            <li><a href="http://vimeo.com/shopify" title="React - Responsive Shopify Theme - Graphite on Vimeo" className="icon-social vimeo ir">Vimeo</a></li>
                        </ul>
                    </div>
                </div>

                <div className="copyright">&copy; 2016 Network New Technoloigies Inc.</div>
            </div>
        )
    }
});

export default Footer;
