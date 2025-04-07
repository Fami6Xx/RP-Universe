# RPUniverse Job System

This document provides detailed information about the RPUniverse job system, including core components, workflow, configuration, and best practices.

## Overview

The RPUniverse job system is a comprehensive framework for creating and managing in-game jobs. It allows server administrators to create custom jobs with different positions, working steps, and sell steps, providing a complete economic ecosystem for roleplay servers.

Key features of the job system include:

- **Job Management**: Create, edit, and delete jobs through in-game commands
- **Position Hierarchy**: Define job positions with different salaries and permissions
- **Working Steps**: Create interactive work locations where players perform job tasks
- **Sell Steps**: Set up locations where players can sell products for money
- **Job Bank**: Manage a shared bank account for each job
- **Salary System**: Automatically pay salaries to job members
- **Boss System**: Assign boss positions with special management privileges
- **Job Types**: Extend job functionality with different job types

## Core Components

### Jobs

Jobs are the central entity in the job system. Each job has:

- A unique name and UUID
- A bank account with shared funds
- A list of positions that players can hold
- A boss menu location where players can interact with the job
- Configuration settings for salaries and permissions
- An optional job type that extends functionality

Jobs are managed by the `JobsHandler` class, which handles loading, saving, and managing all jobs in the system.

### Positions

Positions represent roles within a job. Each position has:

- A name (e.g., "Manager", "Worker")
- A salary amount paid to players in this position
- A working step permission level that determines which working steps the player can use
- A boss flag that grants administrative privileges
- A default flag that marks the position for new members

Positions are organized in a hierarchy, with higher positions having more authority over lower positions. This hierarchy affects who can kick members, change positions, and perform other administrative actions.

### Job Types

Job types extend the functionality of jobs with specialized behaviors. The system uses an interface-based approach:

- `JobType` interface defines the contract for all job types
- Each job type implementation provides specific functionality
- The basic job type (`BasicJobType`) supports working steps and sell steps
- Custom job types can add specialized menus, behaviors, and data

Job types are registered with the `JobsHandler` and can be assigned to jobs through the admin interface.

### Working Steps

Working steps are interactive locations where players perform job tasks. Each working step has:

- A name and description
- One or more working locations
- Required items needed to perform the work
- Possible drops that players can receive
- A permission level that restricts access to certain positions
- A time duration for completing the work
- Optional resource depletion settings

Working steps are created and managed through the job type's admin interface.

### Sell Steps

Sell steps are locations where players can sell items produced from working steps. Each sell step has:

- A name and description
- A location where selling takes place
- An item that can be sold
- A value for the item
- A maximum sell amount
- Percentage distribution between player and job bank
- A time duration for completing the sale

Sell steps complete the economic cycle by converting items into money that goes to both the player and the job bank.

## Job Workflow

The typical workflow in the job system follows these steps:

1. **Job Creation**: An administrator creates a job using the `/createjob` command
2. **Position Setup**: The administrator defines positions with different salaries and permissions
3. **Working Step Creation**: The administrator sets up working steps where job tasks are performed
4. **Sell Step Creation**: The administrator creates sell steps where products can be sold
5. **Member Management**: Players are added to the job and assigned positions
6. **Job Execution**: Players perform tasks at working steps to produce items
7. **Item Selling**: Players sell items at sell steps to earn money
8. **Salary Payment**: The system automatically pays salaries to job members

This workflow creates a complete economic cycle where players work to produce items, sell those items for profit, and receive regular salaries from the job bank.

## Configuration Options

The job system can be configured through the `config.yml` file in the `jobs` section:

```yaml
jobs:
  preferPermissionsOverModeForEdit: true
  neededModeToEditJobs: ADMIN
  needsPermissionToHaveMultipleJobs: true
  maxJobsPerPlayer: 3
  menuRange: 5
  distanceToAddToJob: 5
```

Configuration options include:

- **preferPermissionsOverModeForEdit**: Whether to prefer permissions over player mode for editing jobs
- **neededModeToEditJobs**: The player mode needed to edit jobs (ADMIN or MODERATOR)
- **needsPermissionToHaveMultipleJobs**: Whether players need permission to have multiple jobs
- **maxJobsPerPlayer**: Maximum number of jobs a player can have with the multiple jobs permission
- **menuRange**: The interaction range for job boss menus
- **distanceToAddToJob**: The distance a player must be from the boss to be added to the job

## Commands and Permissions

### Commands

- `/jobs` - View available jobs and manage your job membership
- `/createjob` - Start the job creation process (admin only)
- `/switchjob [jobId]` - Switch between jobs if you have multiple

### Permissions

- `rpu.createjob` - Allows creation of jobs
- `rpu.multiplejobs` - Allows having multiple jobs
- `rpu.admin.jobs` - Grants full administrative access to all jobs
- `rpu.mod.jobs` - Grants moderator access to job management

## Integration with Other Systems

The job system integrates with several other systems in RPUniverse:

- **Economy System**: Jobs interact with the server economy for salaries and sales
- **Hologram System**: Jobs use holograms for boss menus and working steps
- **Menu System**: Jobs provide interactive menus for management
- **Permission System**: Jobs respect server permissions for administrative actions
- **Event System**: Jobs fire events that other plugins can listen for

## Best Practices

Here are some best practices for working with the RPUniverse job system:

1. **Plan your job hierarchy**: Design your positions and permission levels carefully
2. **Balance the economy**: Set appropriate salaries and item values to maintain economic balance
3. **Use job types effectively**: Choose the right job type for your specific needs
4. **Distribute boss positions carefully**: Only give boss positions to trusted players
5. **Monitor job banks**: Regularly check job banks to ensure they have sufficient funds for salaries
6. **Create clear workflows**: Design working steps and sell steps to create intuitive workflows
7. **Use descriptive names**: Give jobs, positions, and steps clear, descriptive names

## Troubleshooting

If you encounter issues with the job system:

1. **Check job readiness**: Use the admin menu to check if jobs are properly configured
2. **Verify permissions**: Ensure players have the correct permissions for their actions
3. **Check job bank balance**: Ensure there's enough money for salaries
4. **Review working step requirements**: Make sure working steps have valid items and locations
5. **Check sell step configuration**: Verify that sell steps have correct item and value settings
6. **Monitor console for errors**: Look for error messages related to the job system
7. **Restart the server**: Some issues may be resolved by a server restart

## Advanced Features

### Custom Job Types

Developers can create custom job types by implementing the `JobType` interface. Custom job types can provide specialized functionality for specific server needs.

### Job Events

The job system fires several events that can be listened for by other plugins:

- `JobLoadedEvent`: Fired when a job is loaded
- `JobDeletedEvent`: Fired when a job is deleted
- `PlayerAddedToJobEvent`: Fired when a player is added to a job
- `PlayerRemovedFromJobEvent`: Fired when a player is removed from a job
- `MoneyAddedToJobBankEvent`: Fired when money is added to a job bank
- `MoneyRemovedFromJobBankEvent`: Fired when money is removed from a job bank

### Resource Depletion

Working steps can be configured with resource depletion settings, simulating the exhaustion of resources over time. This adds realism and encourages players to explore different working locations.

## Conclusion

The RPUniverse job system provides a comprehensive framework for creating and managing in-game jobs. By understanding its components and workflow, server administrators can create engaging economic ecosystems that enhance the roleplay experience.