import React, { Component } from "react";
import ReactDOM from "react-dom";
import OutputNotifications from "../presentational/OutputNotifications.jsx";

class NotificationsContainer extends Component {

  constructor() {
    super();

    this.state = {
      notifications: []
    };

    this.handleNotification = this.handleNotification.bind(this);

    var source = new EventSource(window.location.href + 'notifier/documents');
    source.onmessage = this.handleNotification;
  }

  handleNotification(event) {
    this.state.notifications.push(event.data);
    this.setState({
        notifications: this.state.notifications
    });
  }

  render() {
    const { notifications } = this.state;
    return (
      <div className="view-container">
        <p className="view-title">SSE Push Notifications</p>
        <OutputNotifications
          id="notifications-id"
          value={notifications}
        />
      </div>
    );
  }
}

export default NotificationsContainer;

const wrapper = document.getElementById("notifications-div");
wrapper ? ReactDOM.render(<NotificationsContainer />, wrapper) : false;
