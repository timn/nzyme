import React from 'react';
import moment from "moment";
import Routes from "../../util/Routes";
import BanditContactStatus from "./BanditContactStatus";
import BanditTrackingStatus from "./BanditTrackingStatus";

class BanditsTableRow extends React.Component {

    render() {
        const bandit = this.props.bandit;

        return (
            <tr>
                <td>
                    <a href={Routes.BANDITS.SHOW(bandit.uuid)}>{bandit.name}</a>
                    {bandit.read_only && <i className="fas fa-shield-alt built-in-bandit" title="Built-in bandit"/>}
                </td>
                <td>
                    <BanditContactStatus bandit={bandit} /> <BanditTrackingStatus bandit={bandit} />
                </td>
                <td title={bandit.last_contact ? moment(bandit.last_contact).format() : "never"}>
                    {bandit.last_contact ? moment(bandit.last_contact).fromNow() : "never"}
                </td>
                <td title={moment(bandit.created_at).format()}>{moment(bandit.created_at).fromNow()}</td>
                <td title={moment(bandit.updated_at).format()}>{moment(bandit.updated_at).fromNow()}</td>
            </tr>
        );
    }

}

export default BanditsTableRow;