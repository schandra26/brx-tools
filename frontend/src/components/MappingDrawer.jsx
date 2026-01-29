import PropTypes from 'prop-types';
import '../styles/MappingDrawer.css';

export default function MappingDrawer({ mapping, onClose, onFlag }) {
  return (
    <>
      <div className="drawer-overlay" onClick={onClose}></div>
      <div className="mapping-drawer">
        <div className="drawer-header">
          <h2>{mapping.name}</h2>
          <button className="drawer-close" onClick={onClose}>
            ✕
          </button>
        </div>

        <div className="drawer-content">
          <section className="drawer-section">
            <h3>System Mapping</h3>
            <div className="detail-row">
              <label>Source System:</label>
              <span>{mapping.sourceSystem}</span>
            </div>
            <div className="detail-row">
              <label>Target System:</label>
              <span>{mapping.targetSystem}</span>
            </div>
          </section>

          <section className="drawer-section">
            <h3>Mapping Details</h3>
            <div className="detail-row">
              <label>Transformation Type:</label>
              <span className="badge type-detail">
                {mapping.transformationType || 'N/A'}
              </span>
            </div>
            <div className="detail-row">
              <label>Complexity:</label>
              <span className="badge complexity-detail">
                {mapping.complexity || 'Medium'}
              </span>
            </div>
            <div className="detail-row">
              <label>Status:</label>
              <span className="badge status-detail">
                {mapping.status || 'Active'}
              </span>
            </div>
          </section>

          {mapping.description && (
            <section className="drawer-section">
              <h3>Description</h3>
              <p className="description-text">{mapping.description}</p>
            </section>
          )}

          {mapping.fields && mapping.fields.length > 0 && (
            <section className="drawer-section">
              <h3>Field Mappings ({mapping.fields.length})</h3>
              <div className="fields-list">
                {mapping.fields.map((field, idx) => (
                  <div key={idx} className="field-item">
                    <div className="field-source">{field.source}</div>
                    <div className="field-arrow">→</div>
                    <div className="field-target">{field.target}</div>
                  </div>
                ))}
              </div>
            </section>
          )}

          {mapping.enumerations && mapping.enumerations.length > 0 && (
            <section className="drawer-section">
              <h3>Enumerations ({mapping.enumerations.length})</h3>
              <div className="enumerations-list">
                {mapping.enumerations.map((enumItem, idx) => (
                  <div key={idx} className="enum-item">
                    <span className="enum-key">{enumItem.key}</span>
                    <span className="enum-value">{enumItem.value}</span>
                  </div>
                ))}
              </div>
            </section>
          )}

          <section className="drawer-section">
            <h3>Metadata</h3>
            <div className="detail-row">
              <label>Created:</label>
              <span>
                {mapping.createdAt
                  ? new Date(mapping.createdAt).toLocaleString()
                  : 'N/A'}
              </span>
            </div>
            {mapping.updatedAt && (
              <div className="detail-row">
                <label>Updated:</label>
                <span>
                  {new Date(mapping.updatedAt).toLocaleString()}
                </span>
              </div>
            )}
            {mapping.createdBy && (
              <div className="detail-row">
                <label>Created By:</label>
                <span>{mapping.createdBy}</span>
              </div>
            )}
          </section>
        </div>

        <div className="drawer-footer">
          <button
            className={`flag-button ${mapping.flagged ? 'flagged' : ''}`}
            onClick={() => onFlag(mapping.flagged)}
          >
            {mapping.flagged ? '⚑ Flagged for Review' : '⚑ Flag for Review'}
          </button>
        </div>
      </div>
    </>
  );
}

MappingDrawer.propTypes = {
  mapping: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string,
    sourceSystem: PropTypes.string,
    targetSystem: PropTypes.string,
    transformationType: PropTypes.string,
    complexity: PropTypes.string,
    status: PropTypes.string,
    description: PropTypes.string,
    flagged: PropTypes.bool,
    createdAt: PropTypes.string,
    updatedAt: PropTypes.string,
    createdBy: PropTypes.string,
    fields: PropTypes.array,
    enumerations: PropTypes.array,
  }).isRequired,
  onClose: PropTypes.func.isRequired,
  onFlag: PropTypes.func.isRequired,
};
