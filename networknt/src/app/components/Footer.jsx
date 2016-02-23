import React from 'react';

const Footer = React.createClass({
    render() {
        return (
            <div classNames="container-fluid">
                <div id="footer">
                    <div classNames="footerFloat">
                        <ul>
                            <li><a href="#">About Us</a></li>
                            <li><a href="#">Terms and Conditions</a></li>
                            <li><a href="#">Privacy Policy</a></li>
                            <li><a href="#">Contact Us</a></li>
                        </ul>
                    </div>
                    <div classNames="footerFloat">
                        <ul>
                            <li><a href="#">Support</a></li>
                            <li><a href="#">Abuse Report</a></li>
                            <li><a href="#">FAQ</a></li>
                        </ul>
                    </div>
                    <div classNames="footerFloat">
                        <ul>
                            <li><a href="#">News</a></li>
                            <li><a href="#">Blog</a></li>
                            <li><a href="#">Forum</a></li>
                            <li><a href="#">Catalog</a></li>
                        </ul>
                    </div>
                    <div classNames="footerFloat">
                        <h6>COPYRIGHTS &copy; 2016</h6>
                        <h6>ALL RIGHTS RESERVED.</h6>
                        <h6>Network New Technologies Inc.</h6>
                        <ul>
                            <li><a href="http://twitter.com/shopify" title="React - Responsive Shopify Theme - Graphite on Twitter" classNames="icon-social twitter ir">Twitter</a></li>
                            <li><a href="http://www.facebook.com/shopify" title="React - Responsive Shopify Theme - Graphite on Facebook" classNames="icon-social facebook ir">Facebook</a></li>
                            <li><a href="http://plus.google.com/+shopify" title="React - Responsive Shopify Theme - Graphite on Google+" classNames="icon-social google ir">Google+</a></li>
                            <li><a href="http://www.youtube.com/user/shopify" title="React - Responsive Shopify Theme - Graphite on YouTube" classNames="icon-social youtube ir">YouTube</a></li>
                            <li><a href="http://vimeo.com/shopify" title="React - Responsive Shopify Theme - Graphite on Vimeo" classNames="icon-social vimeo ir">Vimeo</a></li>
                            <li><a href="http://instagram.com/shopify" title="React - Responsive Shopify Theme - Graphite on Instagram" classNames="icon-social instagram ir">Instagram</a></li>
                            <li><a href="http://pinterest.com/shopify/" title="React - Responsive Shopify Theme - Graphite on Pinterest" classNames="icon-social pinterest ir">Pinterest</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        )
    }
});

export default Footer;
