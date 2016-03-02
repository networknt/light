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
                            <li><a href="/page/com-networknt-light-v-about">About Us</a></li>
                            <li><a href="/page/com-networknt-light-v-terms-and-conditions#">Terms and Conditions</a></li>
                            <li><a href="/page/com-networknt-light-v-privacy-policy">Privacy Policy</a></li>
                            <li><a href="/page/com-networknt-light-v-use-policy">Acceptable Use Policy</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li><a href="/page/com-networknt-light-v-support">Support</a></li>
                            <li><a href="/form/com.networknt.light.support.abuse">Abuse Report</a></li>
                            <li><a href="/page/com-networknt-light-v-faq">FAQ</a></li>
                            <li><a href="/page/com-networknt-light-v-contact">Contact Us</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li><a href="/news/All">News</a></li>
                            <li><a href="/blog/All">Blog</a></li>
                            <li><a href="/forum/All">Forum</a></li>
                            <li><a href="/catalog/Hosting">Catalog</a></li>
                        </ul>
                    </div>
                    <div className="footerFloat">
                        <ul>
                            <li><a href="https://twitter.com/networkntcom" title="networknt.com on Twitter" className="icon-social twitter ir">Twitter</a></li>
                            <li><a href="https://www.facebook.com/networkntcom" title="networknt.com on Facebook" className="icon-social facebook ir">Facebook</a></li>
                            <li><a href="https://plus.google.com/u/2/b/111638324561159940003/111638324561159940003/" title="networknt.com on Google+" className="icon-social google ir">Google+</a></li>
                            <li><a href="https://www.youtube.com/channel/UCHCRMWJVXw8iB7zKxF55Byw" title="networknt.com on YouTube" className="icon-social youtube ir">YouTube</a></li>
                        </ul>
                    </div>
                </div>

                <div className="copyright">&copy; 2016 Network New Technoloigies Inc.</div>
            </div>
        )
    }
});

export default Footer;
