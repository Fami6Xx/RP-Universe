# Invoice System User Acceptance Testing Plan

This document outlines the user acceptance testing (UAT) plan for the RPUniverse invoice system. The purpose of UAT is to verify that the system meets the business requirements and is ready for deployment.

## Test Environment

- Minecraft server running Paper 1.14
- RPUniverse plugin installed with all dependencies
- Test accounts with various permissions and job roles
- Economy system configured and functional

## Test Scenarios

### 1. Complete Workflow Testing

#### 1.1 Invoice Creation
- **Test ID**: UAT-CW-001
- **Description**: Test the complete invoice creation workflow
- **Steps**:
  1. Log in as a player with a job boss role
  2. Approach another player within the configured distance
  3. Execute the `/createinvoice` command with valid parameters
  4. Verify the invoice is created successfully
  5. Verify the target player receives a notification
- **Expected Result**: Invoice is created and stored in the system, target player is notified

#### 1.2 Invoice Viewing
- **Test ID**: UAT-CW-002
- **Description**: Test viewing invoices through the menu
- **Steps**:
  1. Log in as a player with pending invoices
  2. Execute the `/invoices` command
  3. Navigate through the paginated menu
  4. Apply filters (received, created, job)
  5. Verify all invoices are displayed correctly
- **Expected Result**: All invoices are displayed with correct information and pagination works

#### 1.3 Invoice Payment
- **Test ID**: UAT-CW-003
- **Description**: Test paying an invoice
- **Steps**:
  1. Log in as a player with pending invoices
  2. Execute the `/invoices` command
  3. Select a pending invoice
  4. Click the pay button
  5. Verify the payment is processed
  6. Verify both players receive notifications
- **Expected Result**: Invoice is marked as paid, money is transferred, both players are notified

#### 1.4 Invoice Deletion
- **Test ID**: UAT-CW-004
- **Description**: Test deleting an invoice
- **Steps**:
  1. Log in as a player who created an invoice
  2. Execute the `/invoices` command
  3. Select a pending invoice
  4. Click the delete button
  5. Verify the invoice is deleted
  6. Verify both players receive notifications
- **Expected Result**: Invoice is marked as deleted, both players are notified

### 2. Feature Verification

#### 2.1 Job Integration
- **Test ID**: UAT-FV-001
- **Description**: Verify job system integration
- **Steps**:
  1. Test invoice creation as a job boss
  2. Test invoice creation as a regular job member
  3. Test invoice creation as a player not in the job
  4. Test job-specific invoice filtering
- **Expected Result**: Only job bosses can create invoices, filtering works correctly

#### 2.2 Economy Integration
- **Test ID**: UAT-FV-002
- **Description**: Verify economy system integration
- **Steps**:
  1. Test paying an invoice with sufficient funds
  2. Test paying an invoice with insufficient funds
  3. Verify transaction logging
- **Expected Result**: Payments work correctly, insufficient funds are handled properly

#### 2.3 Menu System
- **Test ID**: UAT-FV-003
- **Description**: Verify menu system functionality
- **Steps**:
  1. Test pagination with many invoices
  2. Test all filter options
  3. Test action buttons (pay, delete)
  4. Test status indicators
- **Expected Result**: Menu is intuitive and all functions work correctly

#### 2.4 Notification System
- **Test ID**: UAT-FV-004
- **Description**: Verify notification system
- **Steps**:
  1. Test join notifications for pending invoices
  2. Test invoice creation notifications
  3. Test invoice payment notifications
  4. Test invoice deletion notifications
- **Expected Result**: All notifications are displayed correctly

### 3. Edge Cases and Error Handling

#### 3.1 Distance and Visibility
- **Test ID**: UAT-EC-001
- **Description**: Test distance and visibility requirements
- **Steps**:
  1. Test invoice creation when players are too far apart
  2. Test invoice creation when line of sight is blocked
  3. Test with different configuration settings
- **Expected Result**: System enforces distance and visibility requirements correctly

#### 3.2 Permission Handling
- **Test ID**: UAT-EC-002
- **Description**: Test permission handling
- **Steps**:
  1. Test commands with and without required permissions
  2. Test job boss privileges
  3. Test invoice actions by unauthorized players
- **Expected Result**: System enforces permissions correctly

#### 3.3 Invalid Input
- **Test ID**: UAT-EC-003
- **Description**: Test handling of invalid input
- **Steps**:
  1. Test invoice creation with invalid job
  2. Test invoice creation with invalid player
  3. Test invoice creation with invalid amount
  4. Test invoice creation with negative amount
  5. Test invoice creation with decimal amount when not allowed
- **Expected Result**: System provides clear error messages for invalid input

#### 3.4 Data Persistence
- **Test ID**: UAT-EC-004
- **Description**: Test data persistence
- **Steps**:
  1. Create invoices
  2. Restart the server
  3. Verify all invoices are loaded correctly
  4. Test automatic data saving
- **Expected Result**: Data is persisted correctly across server restarts

## Test Execution

1. Each test should be executed by a tester following the steps outlined above
2. Results should be documented, including any issues encountered
3. Issues should be categorized by severity:
   - Critical: Prevents core functionality
   - Major: Significantly impacts usability
   - Minor: Cosmetic or non-essential issues
4. All critical and major issues must be resolved before deployment

## Acceptance Criteria

The invoice system will be considered ready for deployment when:
1. All test scenarios pass successfully
2. No critical issues remain unresolved
3. All major issues have been addressed
4. Documentation is complete and accurate
5. Performance is acceptable under normal load conditions