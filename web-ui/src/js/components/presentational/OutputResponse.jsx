import React from "react";
import PropTypes from "prop-types";

const OutputResponse = ({ id, label, value }) => (
  <div className="output-response-group">
    <label htmlFor={id}>{label}</label>
    <textarea
      className="output-response-control"
      id={id}
      value={value}
      readOnly
      rows="5"
    />
  </div>
);

OutputResponse.propTypes = {
  id: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired
};

export default OutputResponse;
