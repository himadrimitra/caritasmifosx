SET FOREIGN_KEY_CHECKS=0;
INSERT IGNORE  INTO `sms_messages_outbound` (`client_id`, `status_enum`, `mobile_no`, `message`, `submittedon_date`, `external_id`, `campaign_id`, `delivered_on_date`)
SELECT IF(REPLACE(SUBSTRING_INDEX(d.entity_description,' ',1),'clientId:','')*1=0, NULL, REPLACE(SUBSTRING_INDEX(d.entity_description,' ',1),'clientId:','')*1) AS client_id
, IF(d.processed=1,300, 400) AS status_enum
, d.entity_mobile_no AS mobile_no
, IFNULL(d.message, ' ') AS message
, DATE(d.created_on) AS submittedon_date
, d.id AS external_id
, s.id as campaignId
, d.last_modified_on AS delivered_on_date
FROM event_sourcing_details d
left join stretchy_report r on r.report_name = d.report_name
left join sms_campaign s on s.report_id = r.id;
SET FOREIGN_KEY_CHECKS=1;
