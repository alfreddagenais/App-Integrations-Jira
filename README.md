[![Symphony Software Foundation - Incubating](https://cdn.rawgit.com/symphonyoss/contrib-toolbox/master/images/ssf-badge-incubating.svg)](https://symphonyoss.atlassian.net/wiki/display/FM/Incubating)
[![Build Status](https://travis-ci.org/symphonyoss/App-Integrations-Jira.svg?branch=dev)](https://travis-ci.org/symphonyoss/App-Integrations-Jira)
[![Dependencies](https://www.versioneye.com/user/projects/58d049f56893fd004792c870/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58d049f56893fd004792c870)
[![Validation Status](https://scan.coverity.com/projects/12824/badge.svg?flat=1)](https://scan.coverity.com/projects/symphonyoss-app-integrations-jira)
[![codecov](https://codecov.io/gh/symphonyoss/App-Integrations-Jira/branch/dev/graph/badge.svg)](https://codecov.io/gh/symphonyoss/App-Integrations-Jira)


*These informations cover only JIRA specific webhook configuration and the rendering pipeline. For more information about Webhook Integration architecture, development environment, application bootstraping and building, please visit https://github.com/symphonyoss/App-Integrations-Zapier.*

# JIRA Webhook Integration
The JIRA Webhook Integration will allow you to receive notifications in Symphony whenever an issue-related event occurs inside of JIRA.

## How it works
If you are a JIRA admin user, you can configure a JIRA WebHook (as shown in the image below) using the URL generated in the JIRA Webhook Application to begin receiving notifications for the supported events.

![JIRA WebHook Configuration Page](src/docs/sample/sample_webhook_event_selection.png)

## What formats and events it supports and what it produces
Every integration will receive a message sent in a specific format (depending on the system it ingests) and will usually convert it into an internal format before it reaches the Symphony platform. It will also, usually, identify the kind of message based on an "event" identifier, which varies based on the third-party system.

Currently we support the following five events from JIRA: issue created, issue updated, comment created, comment updated, and comment removed. Each one can be enabled when configuring your WebHook in the JIRA system configuration.
Below we will detail one event as an example. This example has the JSON payload sent by JIRA's webhook, the Message ML v2 and EntityJSON generated by the Integration Bridge, and how the message is rendered on Symphony.

### An Example: Issue Created
Here we will show a sample payload of an issue created on Jira, with the received payload, the MessageML generated by the Integration Bridge, and the message as it is rendered in Symphony.

##### Message sent from Jira webhook 
* [Issue Created JSON file](src/docs/sample/jiraCallbackSampleIssueCreatedWithEpic.json)

##### MessageML and JSON entity supported by the Jira Webhook Integration (MessageML v2)
When the Integration Bridge posts messages through the Agent that has version equal or greater than '1.46.0' the
generated Symphony Message must follow the MessageML V2 specification.

The Jira integration on the Integration Bridge parses the JSON payload that Jira sent, and generates messageMLv2 and EntityJSON. 

More information about MessageML V2 specification can be accessed [here](https://symphonyoss.atlassian.net/wiki/display/WGFOS/MessageML+V2+Draft+Proposal+-+For+Discussion)

###### MessageML
This is the messageML v2 that the JIRA integration generates after parsing, which defines the layout of the card and how the front end will render it within Symphony:

```xml
<messageML>
    <div class="entity" data-entity-id="jiraIssue">
        <card class="barStyle" accent="tempo-bg-color--${entity['jiraIssue'].accent!'gray'}" iconSrc="${entity['jiraIssue'].icon.url}">
            <header>
                <p>
                    <img src="${entity['jiraIssue'].issue.priority.iconUrl}" class="icon" />
                    <a class="tempo-text-color--link" href="${entity['jiraIssue'].issue.url}">${entity['jiraIssue'].issue.key}</a>
                    <span class="tempo-text-color--normal">${entity['jiraIssue'].issue.subject} - </span>
                    <span>${entity['jiraIssue'].user.displayName}</span>
                    <span class="tempo-text-color--green">${entity['jiraIssue'].issue.action}</span>
                </p>
            </header>
            <body>
                <div class="labelBackground badge">
                    <p>
                        <#if (entity['jiraIssue'].issue.description)??>
                            <span class="tempo-text-color--secondary">Description:</span>
                            <span class="tempo-text-color--normal">${entity['jiraIssue'].issue.description}</span>
                        </#if>

                        <br/>
                        <span class="tempo-text-color--secondary">Assignee:</span>
                        <#if (entity['jiraIssue'].issue.assignee.id)??>
                            <mention email="${entity['jiraIssue'].issue.assignee.emailAddress}" />
                        <#else>
                            <span class="tempo-text-color--normal">${entity['jiraIssue'].issue.assignee.displayName}</span>
                        </#if>
                    </p>
                    <hr/>
                    <p>
                        <span class="tempo-text-color--secondary">Type:</span>
                        <img src="${entity['jiraIssue'].issue.issueType.iconUrl}" class="icon" />
                        <span class="tempo-text-color--normal">${entity['jiraIssue'].issue.issueType.name}</span>

                        <span class="tempo-text-color--secondary">&#160;&#160;&#160;Priority:</span>
                        <img src="${entity['jiraIssue'].issue.priority.iconUrl}" class="icon" />
                        <span class="tempo-text-color--normal">${entity['jiraIssue'].issue.priority.name}</span>


                        <#if (entity['jiraIssue'].issue.epic)??>
                            <span class="tempo-text-color--secondary">&#160;&#160;&#160;Epic:</span>
                            <a href="${entity['jiraIssue'].issue.epic.link}">${entity['jiraIssue'].issue.epic.name}</a>
                        </#if>

                        <span class="tempo-text-color--secondary">&#160;&#160;&#160;Status:</span>
                        <span class="tempo-bg-color--${entity['jiraIssue'].tokenColor} tempo-text-color--white tempo-token">
                            ${entity['jiraIssue'].issue.status?upper_case}
                        </span>


                        <#if (entity['jiraIssue'].issue.labels)??>
                            <span class="tempo-text-color--secondary">&#160;&#160;&#160;Labels:</span>
                            <#list entity['jiraIssue'].issue.labels as label>
                                <span class="hashTag">#${label.text}</span>
                            </#list>
                        </#if>
                    </p>
                </div>
            </body>
        </card>
    </div>
</messageML>
```
###### Entity JSON
This is the EntityJSON that the JIRA integration generates after parsing, which defines the content of the card that the front-end will use in combination with the MessageML v2 to render the card:
```json
{
  "jiraIssue": {
    "type": "com.symphony.integration.jira.event.v2.state",
    "version": "1.0",
    "accent": "green",
    "tokenColor": "blue",
    "icon": {
      "type": "com.symphony.integration.icon",
      "version": "1.0",
      "url": "https://nexus2.symphony.com/apps/jira/img/jira_logo_rounded.png"
    },
    "user": {
      "type": "com.symphony.integration.user",
      "version": "1.0",
      "emailAddress": "test@symphony.com",
      "displayName": "The creator"
    },
    "issue": {
      "type": "com.symphony.integration.jira.issue",
      "version": "1.0",
      "key": "SAM-25",
      "url": "https://jira.atlassian.com/browse/SAM-25",
      "subject": "Fixing some RabbitMQ health issues",
      "description": "There are some issues with rabbitMQ, it seems that there&apos;s no enough storage to process the carrot queue.",
      "status": "TO DO",
      "action": "Created",
      "issueType": {
        "type": "com.symphony.integration.jira.issueType",
        "version": "1.0",
        "name": "Story",
        "iconUrl": "https://jira.atlassian.com/images/icons/issuetypes/story.svg"
      },
      "priority": {
        "type": "com.symphony.integration.jira.priority",
        "version": "1.0",
        "iconUrl": "https://jira.atlassian.com/images/icons/priorities/highest.svg",
        "name": "Highest"
      },
      "assignee": {
        "type": "com.symphony.integration.user",
        "version": "1.0",
        "emailAddress": "sara.coelho@symphony.com",
        "displayName": "Sara Coelho"
      },
      "epic": {
        "type": "com.symphony.integration.jira.epic",
        "version": "1.0",
        "name": "CP-5",
        "link": "https://jira.atlassian.com/browse/CP-5"
      },
      "labels": [
        {
          "type": "com.symphony.integration.jira.label",
          "version": "1.0",
          "text": "production"
        },
        {
          "type": "com.symphony.integration.jira.label",
          "version": "1.0",
          "text": "rabbitmq"
        },
        {
          "type": "com.symphony.integration.jira.label",
          "version": "1.0",
          "text": "easteregg"
        }
      ]
    }
  }
}
```
##### Message rendered in Symphony

![Issue Created rendered](src/docs/sample/sample_issue_created_with_epic_rendered_v2.png)

### Messages color mapping
To give a better visual information, JIRA's messages have specific flair color (vertical bar on the left) according to the issue type, as follows:

|       Issue Type        |    Color    |
|:------------------------|:-----------:|
|Bug                      |     Red     |
|Incident                 |     Red     |
|Support Issue            |     Red     |
|Incident Severity 1      |     Red     |
|||
|Epic                     |    Purple   |
|Incident Severity 4      |    Purple   |
|Documentation            |    Purple   |
|||
|Story                    |    Green    |
|New Feature              |    Green    |
|Improvement              |    Green    |
|Change Request           |    Green    |
|||
|Spike                    |    Orange   |
|Problem                  |    Orange   |
|Incident Severity 2      |    Orange   |
|Incident Severity 3      |    Orange   |
|||
|Task                     |     Blue    |

# 2-way integration
Besides the capacity of receiving notifications through Webhooks, some integrations (Apps) can also perform actions on their instances. Currently, there are two availables actions: assign a ticket to another user and comment on a ticket. Before we go into these actions, let us take a look on how to configure this funcionality.

## Installation
Please follow [these steps](https://github.com/SymphonyOSF/Apps/blob/README.md#2-way-integration) to install the certificates and prepare your JIRA integration to work as a 2-way integration.

## Configure the application link
After you have configured the Integration Bridge, it is time to tell your JIRA instance that you are ready to go! Please follow [these steps](https://integrations.symphony.com/v1.0/docs/jira-application-link-configuration#section-setting-up-the-application-link-for-2-way-integration) in order to do it.
