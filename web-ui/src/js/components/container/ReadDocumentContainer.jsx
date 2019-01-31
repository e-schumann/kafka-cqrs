import React, { Component } from "react";
import ReactDOM from "react-dom";
import InputId from "../presentational/InputId.jsx";
import InputButton from "../presentational/InputButton.jsx";

class ReadDocumentContainer extends Component {
  constructor() {
    super();
    this.state = {
      id: ""
    };
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    this.setState({ [event.target.id]: event.target.value });
  }

  render() {
    const { id } = this.state;
    return (
      <form id="read-document-form">
        <InputId
          id="read-document-id"
          label="Id:"
          value={id}
          handleChange={this.handleChange}
        />
        <InputButton
          id="read-document-button"
          value="Read Document"
          handleChange={this.handleChange}
        />
      </form>
    );
  }
}

export default ReadDocumentContainer;

const wrapper = document.getElementById("read-document-div");
wrapper ? ReactDOM.render(<ReadDocumentContainer />, wrapper) : false;
